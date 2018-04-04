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

import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.distributed.criteria.MSEImpurityMeasure;
import com.dmitrievanthony.tree.core.distributed.criteria.MSEImpurityMeasureCalculator;
import com.dmitrievanthony.tree.core.distributed.criteria.ImpurityMeasureCalculator;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import java.util.function.Predicate;

public class DistributedDecisionTreeRegressor extends DistributedDecisionTree<MSEImpurityMeasure> {

    public DistributedDecisionTreeRegressor(int maxDeep, double minImpurityDecrease) {
        super(maxDeep, minImpurityDecrease);
    }

    @Override LeafNode createLeafNode(Dataset dataset, Predicate<double[]> pred) {
        double[] aa = dataset.compute(part -> {
            double[] res = new double[2];

            double mean = 0;
            int cnt = 0;
            for (int i = 0; i < part.getLabels().length; i++) {
                if (pred.test(part.getFeatures()[i])) {
                    mean += part.getLabels()[i];
                    cnt++;
                }
            }
            mean = mean / cnt;

            res[0] = mean;
            res[1] = cnt;

            return res;
        }, this::reduce);

        return new LeafNode(aa[0]);
    }

    @Override ImpurityMeasureCalculator<MSEImpurityMeasure> getImpurityMeasureCalculator(Dataset dataset) {
        return new MSEImpurityMeasureCalculator();
    }

    private double[] reduce(double[] a, double[] b) {
        if (a == null)
            return b;
        else if (b == null)
            return a;
        else {
            double aMean = a[0];
            double aCnt = a[1];
            double bMean = b[0];
            double bCnt = b[1];

            double mean = (aMean * aCnt + bMean * bCnt) / (aCnt + bCnt);

            return new double[]{mean, aCnt + bCnt};
        }
    }
}
