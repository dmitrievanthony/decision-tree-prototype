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
import com.dmitrievanthony.tree.core.local.criteria.SplittingCriteria;
import java.util.Arrays;
import java.util.Optional;

public abstract class LocalDecisionTree {

    private final SplittingCriteria criteria;

    private final int maxDeep;

    private final double minImpurityDecrease;

    public LocalDecisionTree(SplittingCriteria criteria, int maxDeep, double minImpurityDecrease) {
        this.criteria = criteria;
        this.maxDeep = maxDeep;
        this.minImpurityDecrease = minImpurityDecrease;
    }

    public Node fit(double[][] features, double[] labels) {
        return fit(features, labels, 0);
    }

    private Node fit(double[][] features, double[] labels, int deep) {
        if (deep >= maxDeep)
            return createLeafNode(labels);

        int bestCol = -1;
        SplitPoint bestSplitPnt = null;

        for (int col = 0; col < features[0].length; col++) {
            Utils.quickSort(features, labels, col);

            SplitPoint splitPnt = criteria.findBestSplit(labels);

            if (bestSplitPnt == null || splitPnt.getCriteriaVal() < bestSplitPnt.getCriteriaVal()) {
                bestSplitPnt = splitPnt;
                bestCol = col;
            }
        }

        Utils.quickSort(features, labels, bestCol);

        double[][] leftFeatures = Arrays.copyOfRange(features, 0, bestSplitPnt.getLeftSize());
        double[][] rightFeatures = Arrays.copyOfRange(features, bestSplitPnt.getLeftSize(), features.length);

        double[] leftLabels = Arrays.copyOfRange(labels, 0, bestSplitPnt.getLeftSize());
        double[] rightLabels = Arrays.copyOfRange(labels, bestSplitPnt.getLeftSize(), labels.length);

        if (leftFeatures.length == 0)
            return createLeafNode(labels);

        return new ConditionalNode(
            bestCol,
            (leftFeatures[leftFeatures.length - 1][bestCol] + rightFeatures[0][bestCol]) / 2,
            fit(rightFeatures, rightLabels, deep + 1),
            fit(leftFeatures, leftLabels, deep + 1)
        );
    }

    abstract LeafNode createLeafNode(double[] labels);
}
