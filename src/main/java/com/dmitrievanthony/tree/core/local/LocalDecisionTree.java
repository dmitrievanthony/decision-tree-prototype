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

package com.dmitrievanthony.tree.core.local;

import com.dmitrievanthony.tree.core.ConditionalNode;
import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.utils.Utils;
import com.dmitrievanthony.tree.core.local.criteria.SplitPoint;
import com.dmitrievanthony.tree.core.local.criteria.SplitCalculator;
import java.util.Arrays;

/**
 * Local decision tree trainer.
 */
public abstract class LocalDecisionTree {
    /** Split calculator. */
    private final SplitCalculator splitCalc;

    /** Max tree deep. */
    private final int maxDeep;

    /** Min impurity decrease. */
    private final double minImpurityDecrease;

    /**
     * Constructs a new instance of local decision tree trainer.
     *
     * @param splitCalc Split calculator.
     * @param maxDeep Max tree deep.
     * @param minImpurityDecrease Min impurity decrease.
     */
    public LocalDecisionTree(SplitCalculator splitCalc, int maxDeep, double minImpurityDecrease) {
        this.splitCalc = splitCalc;
        this.maxDeep = maxDeep;
        this.minImpurityDecrease = minImpurityDecrease;
    }

    /**
     * Builds a new tree trained on the specified features and labels.
     *
     * @param features Features.
     * @param labels Labels.
     * @return Decision tree.
     */
    public Node fit(double[][] features, double[] labels) {
        return split(features, labels, 0);
    }

    /**
     * Splits the features and labels, and returns decision tree node.
     *
     * @param features Features.
     * @param labels Labels.
     * @param deep Current decision tree deep.
     * @return Decision tree node.
     */
    private Node split(double[][] features, double[] labels, int deep) {
        if (deep >= maxDeep)
            return createLeafNode(labels);

        int bestCol = -1;
        SplitPoint bestSplitPnt = null;

        for (int col = 0; col < features[0].length; col++) {
            Utils.quickSort(features, labels, col);

            SplitPoint splitPnt = splitCalc.findBestSplit(labels, minImpurityDecrease);

            if (bestSplitPnt == null || splitPnt.getImpurityVal() < bestSplitPnt.getImpurityVal()) {
                bestSplitPnt = splitPnt;
                bestCol = col;
            }
        }

        if (bestSplitPnt == null)
            return createLeafNode(labels);

        Utils.quickSort(features, labels, bestCol);

        if (bestSplitPnt.getLeftSize() == 0)
            return createLeafNode(labels);

        double[][] leftFeatures = Arrays.copyOfRange(features, 0, bestSplitPnt.getLeftSize());
        double[][] rightFeatures = Arrays.copyOfRange(features, bestSplitPnt.getLeftSize(), features.length);

        double[] leftLabels = Arrays.copyOfRange(labels, 0, bestSplitPnt.getLeftSize());
        double[] rightLabels = Arrays.copyOfRange(labels, bestSplitPnt.getLeftSize(), labels.length);

        return new ConditionalNode(
            bestCol,
            (leftFeatures[leftFeatures.length - 1][bestCol] + rightFeatures[0][bestCol]) / 2,
            split(rightFeatures, rightLabels, deep + 1),
            split(leftFeatures, leftLabels, deep + 1)
        );
    }

    /**
     * Creates a leaf node.
     *
     * @param labels Labels.
     * @return Leaf node.
     */
    abstract LeafNode createLeafNode(double[] labels);
}
