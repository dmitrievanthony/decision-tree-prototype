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

package com.dmitrievanthony.tree.core.local.criteria;

import java.util.HashMap;
import java.util.Map;

/**
 * Split calculator used Gini impurity measure which is calculated the following way:
 * {@code \frac{1}{L}\sum_{i=0}^{n}l^2 + \frac{1}{R}\sum_{i=0}^{n}r^2}.
 */
public class GiniSplitCalculator implements SplitCalculator {
    /** {@inheritDoc} */
    @Override public SplitPoint findBestSplit(double[] labels) {
        if (labels.length == 0)
            return null;

        Map<Double, Integer> left = new HashMap<>();
        Map<Double, Integer> right = new HashMap<>();

        for (double lb : labels) {
            if (right.containsKey(lb))
                right.put(lb, right.get(lb) + 1);
            else
                right.put(lb, 1);
        }

        double res = 0;
        for (Integer c : right.values())
            res += Math.pow(c, 2);

        double leftVal = 0;
        double rightVal = res / labels.length;

        SplitPoint bestSplitPnt = new SplitPoint(0, leftVal + rightVal);

        for (int leftSize = 1; leftSize < labels.length; leftSize++) {
            double lb = labels[leftSize - 1];
            int featureCntLeft = left.containsKey(lb) ? left.get(lb) + 1 : 1;
            int featureCntRight = right.containsKey(lb) ? right.get(lb) - 1 : 0;

            left.put(lb, featureCntLeft);

            if (featureCntRight == 0)
                right.remove(lb);
            else
                right.put(lb, featureCntRight);

            leftVal = leftVal * (leftSize - 1);
            rightVal = rightVal * (labels.length - leftSize + 1);

            leftVal = leftVal + 2 * featureCntLeft - 1;
            rightVal = rightVal - 2 * featureCntRight - 1;

            leftVal = leftVal / leftSize;
            rightVal = rightVal / (labels.length - leftSize);

            if (leftVal + rightVal > bestSplitPnt.getImpurityVal())
                bestSplitPnt = new SplitPoint(leftSize, leftVal + rightVal);
        }

        return new SplitPoint(bestSplitPnt.getLeftSize(), -bestSplitPnt.getImpurityVal());
    }
}
