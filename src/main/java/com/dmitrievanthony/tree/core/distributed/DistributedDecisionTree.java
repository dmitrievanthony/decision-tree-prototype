/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dmitrievanthony.tree.core.distributed;

import com.dmitrievanthony.tree.core.ConditionalNode;
import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import com.dmitrievanthony.tree.core.distributed.criteria.ImpurityMeasureCalculator;
import com.dmitrievanthony.tree.core.distributed.criteria.ImpurityMeasure;
import com.dmitrievanthony.tree.core.distributed.criteria.StepFunction;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Distributed decision tree trainer that allows to fit trees using row-partitioned dataset.
 *
 * @param <T> Type of impurity measure.
 */
public abstract class DistributedDecisionTree<T extends ImpurityMeasure<T>> {
    /** Max tree deep. */
    private final int maxDeep;

    /** Min impurity decrease. */
    private final double minImpurityDecrease;

    /**
     * Constructs a new distributed decision tree trainer.
     *
     * @param maxDeep Max tree deep.
     * @param minImpurityDecrease Min impurity decrease.
     */
    public DistributedDecisionTree(int maxDeep, double minImpurityDecrease) {
        this.maxDeep = maxDeep;
        this.minImpurityDecrease = minImpurityDecrease;
    }

    /**
     * Builds a new tree trained on the specified dataset.
     *
     * @param dataset Dataset.
     * @return Decision tree.
     */
    public Node fit(Dataset dataset) {
        return split(dataset, e -> true, 0, getImpurityMeasureCalculator(dataset));
    }

    /**
     * Creates a leaf node.
     *
     * @param dataset Dataset.
     * @param pred Decision tree node predicate.
     * @return Leaf node.
     */
    abstract LeafNode createLeafNode(Dataset dataset, Predicate<double[]> pred);

    /**
     * Returns impurity measure calculator.
     *
     * @param dataset Dataset.
     * @return Impurity measure calculator.
     */
    abstract ImpurityMeasureCalculator<T> getImpurityMeasureCalculator(Dataset dataset);

    /**
     * Splits the node specified by the given dataset and predicate and returns decision tree node.
     *
     * @param dataset Dataset.
     * @param pred Decision tree node predicate.
     * @param deep Current tree deep.
     * @param impurityCalc Impurity measure calculator.
     * @return Decision tree node.
     */
    private Node split(Dataset dataset, Predicate<double[]> pred, int deep, ImpurityMeasureCalculator<T> impurityCalc) {
        if (deep >= maxDeep)
            return createLeafNode(dataset, pred);

        StepFunction<T>[] criterionFunctions = calculateImpurityForAllColumns(dataset, pred, impurityCalc);

        if (criterionFunctions == null)
            return createLeafNode(dataset, pred);

        SplitPoint splitPnt = calculateBestSplitPoint(criterionFunctions);

        if (splitPnt == null)
            return createLeafNode(dataset, pred);

        return new ConditionalNode(
            splitPnt.col,
            splitPnt.threshold,
            split(dataset, updatePredicateForThenNode(pred, splitPnt), deep + 1, impurityCalc),
            split(dataset, updatePredicateForElseNode(pred, splitPnt), deep + 1, impurityCalc)
        );
    }

    /**
     * Calculates impurity measure functions for all columns for the node specified by the given dataset and predicate.
     *
     * @param dataset Dataset.
     * @param pred Decision tree node predicate.
     * @param impurityCalc Impurity measure calculator.
     * @return Array of impurity measure functions for all columns.
     */
    private StepFunction<T>[] calculateImpurityForAllColumns(Dataset dataset, Predicate<double[]> pred,
        ImpurityMeasureCalculator<T> impurityCalc) {
        return dataset.compute(
            part -> {
                double[][] allFeatures = part.getFeatures();
                double[] allLabels = part.getLabels();

                int nodeSize = 0;
                for (int i = 0; i < allFeatures.length; i++)
                    nodeSize += pred.test(allFeatures[i]) ? 1 : 0;

                if (nodeSize != 0) {
                    double[][] nodeFeatures = new double[nodeSize][];
                    double[] nodeLabels = new double[nodeSize];

                    int ptr = 0;
                    for (int i = 0; i < allFeatures.length; i++) {
                        if (pred.test(allFeatures[i])) {
                            nodeFeatures[ptr] = allFeatures[i];
                            nodeLabels[ptr] = allLabels[i];
                            ptr++;
                        }
                    }

                    return impurityCalc.calculate(nodeFeatures, nodeLabels);
                }

                return null;
            },
            this::reduce
        );
    }

    /**
     * Calculates best split point.
     *
     * @param criterionFunctions  Array of impurity measure functions for all columns.
     * @return Best split point.
     */
    private SplitPoint calculateBestSplitPoint(StepFunction<T>[] criterionFunctions) {
        SplitPoint res = null;

        for (int col = 0; col < criterionFunctions.length; col++) {
            StepFunction<T> criterionFunctionForCol = criterionFunctions[col];

            double[] arguments = criterionFunctionForCol.getX();
            T[] values = criterionFunctionForCol.getY();

            for (int leftSize = 1; leftSize < values.length - 1; leftSize++) {
                if ((values[0].impurity() - values[leftSize].impurity()) > minImpurityDecrease && (res == null || values[leftSize].compareTo(res.val) < 0))
                    res = new SplitPoint(values[leftSize], col, calculateThreshold(arguments, leftSize));
            }
        }

        return res;
    }

    /**
     * Merges two arrays gotten from two partitions.
     *
     * @param a First step function.
     * @param b Second step function.
     * @return Merged step function.
     */
    private StepFunction<T>[] reduce(StepFunction<T>[] a, StepFunction<T>[] b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        else {
            StepFunction<T>[] res = Arrays.copyOf(a, a.length);
            for (int i = 0; i < res.length; i++)
                res[i] = res[i].add(b[i]);
            return res;
        }
    }

    /**
     * Calculates threshold based on the given step function arguments and split point (specified left size).
     *
     * @param arguments Step function arguments.
     * @param leftSize Split point (left size).
     * @return Threshold.
     */
    private double calculateThreshold(double[] arguments, int leftSize) {
        return (arguments[leftSize] + arguments[leftSize + 1]) / 2.0;
    }

    /**
     * Constructs a new predicate for "then" node based on the parent node predicate and split point.
     *
     * @param pred Parent node predicate.
     * @param splitPnt Split point.
     * @return Predicate for "then" node.
     */
    private Predicate<double[]> updatePredicateForThenNode(Predicate<double[]> pred, SplitPoint splitPnt) {
        return pred.and(f -> f[splitPnt.col] > splitPnt.threshold);
    }

    /**
     * Constructs a new predicate for "else" node based on the parent node predicate and split point.
     *
     * @param pred Parent node predicate.
     * @param splitPnt Split point.
     * @return Predicate for "else" node.
     */
    private Predicate<double[]> updatePredicateForElseNode(Predicate<double[]> pred, SplitPoint splitPnt) {
        return pred.and(f -> f[splitPnt.col] <= splitPnt.threshold);
    }

    /**
     * Util class that represents split point.
     */
    private class SplitPoint {
        /** Split point impurity measure value. */
        private final T val;

        /** Column. */
        private final int col;

        /** Threshold. */
        private final double threshold;

        /**
         * Constructs a new instance of split point.
         *
         * @param val Split point impurity measure value.
         * @param col Column.
         * @param threshold Threshold.
         */
        public SplitPoint(T val, int col, double threshold) {
            this.val = val;
            this.col = col;
            this.threshold = threshold;
        }
    }
}
