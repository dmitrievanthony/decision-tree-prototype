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

public class MSESplittingCriteria implements SplittingCriteria<MSEImpurityMeasure> {

    @SuppressWarnings("unchecked")
    @Override public StepFunction<MSEImpurityMeasure>[] calculate(double[][] data, double[] labels) {
        StepFunction<MSEImpurityMeasure>[] res = new StepFunction[data[0].length];

        for (int col = 0; col < res.length; col++) {
            Utils.quickSort(data, labels, col);

            double[] x = new double[data.length + 1];
            MSEImpurityMeasure[] y = new MSEImpurityMeasure[data.length + 1];

            x[0] = Double.NEGATIVE_INFINITY;

            for (int i = 0; i <= data.length; i++) {
                double ly = 0;
                double lyy = 0;
                double ry = 0;
                double ryy = 0;

                for (int j = 0; j < i; j++) {
                    ly += labels[j];
                    lyy += Math.pow(labels[j], 2);
                }

                for (int j = i; j < labels.length; j++) {
                    ry += labels[j];
                    ryy += Math.pow(labels[j], 2);
                }

                if (i < data.length)
                    x[i + 1] = data[i][col];
                y[i] = new MSEImpurityMeasure(ly, lyy, i, ry, ryy, data.length - i);
            }

            res[col] = new StepFunction(x, y, MSEImpurityMeasure.class);
        }

        return res;
    }
}
