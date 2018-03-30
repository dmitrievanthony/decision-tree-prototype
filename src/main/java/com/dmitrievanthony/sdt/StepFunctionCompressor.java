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

package com.dmitrievanthony.sdt;

import java.util.ArrayList;
import java.util.List;

public class StepFunctionCompressor {

    public static StepFunction compress(StepFunction function) {
        double[] x = function.getX();
        double[][] y = function.getY();

        Utils.quickSort(x, y);

        List<StepFunctionPoint> points = new ArrayList<>();
        for (int i = 0; i < x.length; i++)
            points.add(new StepFunctionPoint(x[i], y[i]));

        points = compress(points);

        double[] resX = new double[points.size()];
        double[][] resY = new double[points.size()][];

        for (int i = 0; i < points.size(); i++) {
            StepFunctionPoint pnt = points.get(i);
            resX[i] = pnt.x;
            resY[i] = pnt.y;
        }

        return new StepFunction(resX, resY);
    }

    private static List<StepFunctionPoint> compress(List<StepFunctionPoint> points) {
        List<StepFunctionPoint> res = new ArrayList<>();


        double[] min = new double[6];
        double[] max = new double[6];
        for (StepFunctionPoint pnt : points) {
            for (int i = 0; i < pnt.y.length; i++) {
                double y = pnt.y[i];
                if (y < min[i])
                    min[i] = y;
                if (y > max[i])
                    max[i] = y;
            }
        }

        StepFunctionPoint prevPnt = null;
        for (StepFunctionPoint pnt : points) {
            boolean skip = false;
            if (prevPnt != null) {
                skip = true;
                for (int i = 0; i < max.length; i++) {
                    if ((1.0 * Math.abs(pnt.y[i] - prevPnt.y[i]) / (max[i] - min[i])) > 0.1) {
                        skip = false;
                        break;
                    }
                }
            }

            if (!skip) {
                res.add(pnt);
                prevPnt = pnt;
            }
        }

        System.out.println("Compress : " + 1.0 * points.size() / res.size());

        return res;
    }

    private static class StepFunctionPoint {

        private final double x;

        private final double[] y;

        public StepFunctionPoint(double x, double[] y) {
            this.x = x;
            this.y = y;
        }
    }
}
