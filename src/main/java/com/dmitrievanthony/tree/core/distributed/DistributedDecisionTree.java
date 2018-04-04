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

public abstract class DistributedDecisionTree<T extends ImpurityMeasure<T>> {

    private final int maxDeep;

    private final double minImpurityDecrease;

    public DistributedDecisionTree(int maxDeep, double minImpurityDecrease) {
        this.maxDeep = maxDeep;
        this.minImpurityDecrease = minImpurityDecrease;
    }

    public Node fit(Dataset dataset) {
        ImpurityMeasureCalculator<T> splittingCriteria = getSplittingCriteria(dataset);

        return fit(dataset, e -> true, 0, splittingCriteria);
    }

    abstract LeafNode createLeafNode(Dataset dataset, Predicate<double[]> pred);

    abstract ImpurityMeasureCalculator<T> getSplittingCriteria(Dataset dataset);

    private Node fit(Dataset dataset, Predicate<double[]> pred, int deep, ImpurityMeasureCalculator<T> splittingCriteria) {
        if (deep >= maxDeep)
            return createLeafNode(dataset, pred);

        StepFunction<T>[] criterionFunctions = calculateCriterionForAllColumns(dataset, pred, splittingCriteria);

        SplitPoint splitPnt = calculateBestSplitPoint(criterionFunctions);

        return new ConditionalNode(
            splitPnt.col,
            splitPnt.threshold,
            fit(dataset, updatePredicateForThenNode(pred, splitPnt), deep + 1, splittingCriteria),
            fit(dataset, updatePredicateForElseNode(pred, splitPnt), deep + 1, splittingCriteria)
        );
    }

    private StepFunction<T>[] calculateCriterionForAllColumns(Dataset dataset, Predicate<double[]> pred, ImpurityMeasureCalculator<T> splittingCriteria) {
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

                    return splittingCriteria.calculate(nodeFeatures, nodeLabels);
                }

                return null;
            },
            this::reduce
        );
    }

    private SplitPoint calculateBestSplitPoint(StepFunction<T>[] criterionFunctions) {
        SplitPoint res = null;

        for (int col = 0; col < criterionFunctions.length; col++) {
            StepFunction<T> criterionFunctionForCol = criterionFunctions[col];

            double[] arguments = criterionFunctionForCol.getX();
            T[] values = criterionFunctionForCol.getY();

            for (int leftSize = 1; leftSize < values.length - 1; leftSize++) {
                if (res == null || values[leftSize].compareTo(res.val) > 0)
                    res = new SplitPoint(values[leftSize], col, calculateThreshold(arguments, leftSize));
            }
        }

        return res;
    }

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

    private double calculateThreshold(double[] arguments, int leftSize) {
        return (arguments[leftSize] + arguments[leftSize + 1]) / 2.0;
    }

    private Predicate<double[]> updatePredicateForThenNode(Predicate<double[]> pred, SplitPoint splitPnt) {
        return pred.and(f -> f[splitPnt.col] > splitPnt.threshold);
    }

    private Predicate<double[]> updatePredicateForElseNode(Predicate<double[]> pred, SplitPoint splitPnt) {
        return pred.and(f -> f[splitPnt.col] <= splitPnt.threshold);
    }

    private class SplitPoint {

        private final T val;

        private final int col;

        private final double threshold;

        public SplitPoint(T val, int col, double threshold) {
            this.val = val;
            this.col = col;
            this.threshold = threshold;
        }
    }
}
