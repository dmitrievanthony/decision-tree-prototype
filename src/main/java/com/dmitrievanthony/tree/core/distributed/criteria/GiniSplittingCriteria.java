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

package com.dmitrievanthony.tree.core.distributed.criteria;

import com.dmitrievanthony.tree.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GiniSplittingCriteria implements SplittingCriteria<GiniImpurityMeasure> {

    private final Map<Double, Integer> map;

    public GiniSplittingCriteria(Map<Double, Integer> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Override public StepFunction<GiniImpurityMeasure>[] calculate(double[][] data, double[] labels) {

        StepFunction<GiniImpurityMeasure>[] res = new StepFunction[data[0].length];

        for (int col = 0; col < res.length; col++) {
            Utils.quickSort(data, labels, col);

            List<Double> x = new ArrayList<>();
            List<GiniImpurityMeasure> y = new ArrayList<>();

            x.add(Double.NEGATIVE_INFINITY);

            for (int i = 0; i <= data.length; i++) {
                if (i > 0 && i < data.length && data[i][col] == data[i - 1][col])
                    continue;

                long[] left = new long[map.size()];
                long[] right = new long[map.size()];

                for (int j = 0; j < i; j++) {
                    int idx = map.get(labels[j]);
                    left[idx]++;
                }

                for (int j = i; j < labels.length; j++) {
                    int idx = map.get(labels[j]);
                    right[idx]++;
                }

                if (i < data.length)
                    x.add(data[i][col]);

                y.add(new GiniImpurityMeasure(left, right));
            }

            double[] xx = new double[x.size()];
            GiniImpurityMeasure[] yy = new GiniImpurityMeasure[y.size()];

            for (int i = 0; i < x.size(); i++)
                xx[i] = x.get(i);

            for (int i = 0; i < y.size(); i++)
                yy[i] = y.get(i);

            res[col] = new StepFunction(xx, yy, GiniImpurityMeasure.class);
        }

        return res;
    }
}
