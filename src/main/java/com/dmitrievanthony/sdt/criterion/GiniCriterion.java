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

package com.dmitrievanthony.sdt.criterion;

import java.util.HashMap;
import java.util.Map;

public class GiniCriterion implements Criterion<GiniCriterion.GiniCriterionValue> {

    @Override public GiniCriterionValue calculate(double[] labels, int lSize, GiniCriterionValue prevVal) {
        if (lSize == 0) {
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


            return new GiniCriterionValue(left, right, 0, res / labels.length);
        }
        else {
            Map<Double, Integer> left = prevVal.left;
            Map<Double, Integer> right = prevVal.right;

            double lb = labels[lSize - 1];
            int featureCntLeft = left.containsKey(lb) ? left.get(lb) + 1 : 1;
            int featureCntRight = right.containsKey(lb) ? right.get(lb) - 1 : 0;

            left.put(lb, featureCntLeft);

            if (featureCntRight == 0)
                right.remove(lb);
            else
                right.put(lb, featureCntRight);

            double lRes = prevVal.leftVal * (lSize - 1);
            double rRes = prevVal.rightVal * (labels.length - lSize + 1);

            lRes = lRes + 2 * featureCntLeft - 1;
            rRes = rRes - 2 * featureCntRight - 1;

            lRes = lRes / lSize;
            rRes = rRes / (labels.length - lSize);

            return new GiniCriterionValue(left, right, lRes, rRes);
        }
    }

    public static class GiniCriterionValue implements Comparable<GiniCriterionValue> {

        private final Map<Double, Integer> left;

        private final Map<Double, Integer> right;

        private final double leftVal;

        private final double rightVal;

        public GiniCriterionValue(Map<Double, Integer> left, Map<Double, Integer> right, double leftVal,
            double rightVal) {
            this.left = left;
            this.right = right;
            this.leftVal = leftVal;
            this.rightVal = rightVal;
        }

        @Override public int compareTo(GiniCriterionValue o) {
            double val = leftVal + rightVal;
            double oVal = o.leftVal + o.rightVal;
            return Double.compare(oVal, val);
        }
    }
}
