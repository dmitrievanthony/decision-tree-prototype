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
import com.dmitrievanthony.tree.core.local.criteria.MSESplitCalculator;
import com.dmitrievanthony.tree.core.local.criteria.SplitCalculator;

/**
 * Decision tree regressor based on local decision tree trainer.
 */
public class LocalDecisionTreeRegressor extends LocalDecisionTree {
    /** Default split calculator used for regression. */
    private static final SplitCalculator defaultSplitCalc = new MSESplitCalculator();

    /**
     * Constructs a new instance of local decision tree regressor.
     *
     * @param maxDeep Max tree deep.
     * @param minImpurityDecrease Min impurity decrease.
     */
    public LocalDecisionTreeRegressor(int maxDeep, double minImpurityDecrease) {
        super(defaultSplitCalc, maxDeep, minImpurityDecrease);
    }

    /** {@inheritDoc} */
    @Override LeafNode createLeafNode(double[] labels) {
        double mean = 0;
        for (int i = 0; i < labels.length; i++)
            mean += labels[i];
        mean = mean / labels.length;

        return new LeafNode(mean);
    }
}
