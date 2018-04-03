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

import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.local.criteria.GiniSplittingCriteria;
import com.dmitrievanthony.tree.core.local.criteria.SplittingCriteria;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LocalDecisionTreeClassifier extends LocalDecisionTree {

    private static final double PROBABILITY_THRESHOLD = 0.95;

    private static final int DEEP_THRESHOLD = 100;

    private static final SplittingCriteria defaultSplittingCriteria = new GiniSplittingCriteria();

    public LocalDecisionTreeClassifier(int maxDeep, double minImpurityDecrease) {
        super(defaultSplittingCriteria, maxDeep, minImpurityDecrease);
    }

    @Override LeafNode createLeafNode(double[] labels) {
        Map<Double, Integer> cnt = new HashMap<>();
        for (double label : labels) {
            if (cnt.containsKey(label))
                cnt.put(label, cnt.get(label) + 1);
            else
                cnt.put(label, 1);
        }

        double bestVal = 0;
        int bestCnt = -1;

        for (Map.Entry<Double, Integer> e : cnt.entrySet()) {
            if (e.getValue() > bestCnt) {
                bestCnt = e.getValue();
                bestVal = e.getKey();
            }
        }

        return new LeafNode(bestVal);
    }
}
