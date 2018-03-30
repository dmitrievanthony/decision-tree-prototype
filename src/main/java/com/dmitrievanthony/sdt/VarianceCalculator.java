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

public class VarianceCalculator {

    public static double[][][] toNumeric(StepFunction[] a) {
        double[][][] res = new double[a.length][][];
        for (int i = 0; i < a.length; i++)
            res[i] = toNumeric(a[i]);
        return res;
    }

    public static double[][] toNumeric(StepFunction a) {
        double[] x = a.getX();
        double[][] y = a.getY();

        double[] res = new double[x.length];
        for (int i = 0; i < res.length; i++) {
            double ly = y[i][0];
            double lyy = y[i][1];
            double lc = y[i][2];
            double ry = y[i][3];
            double ryy = y[i][4];
            double rc = y[i][5];

            double lVar = lyy - 2.0 * ly / lc * ly + Math.pow(ly / lc, 2) * lc;
            double rVar = ryy - 2.0 * ry / rc * ry + Math.pow(ry / rc, 2) * rc;

            res[i] = lVar + rVar;
        }

        return new double[][]{x, res};
    }

    public static StepFunction[] add(StepFunction[][] functions) {
        StepFunction[] res = functions[0];
        for (int i = 1; i < functions.length; i++) {
            for (int j = 0; j < res.length; j++)
                res[j] = res[j].add(functions[i][j]);
        }

        return res;
    }

    public static StepFunction[] calculateVariance(double[][] data, double[] labels) {
        StepFunction[] res = new StepFunction[data[0].length];

        for (int col = 0; col < res.length; col++) {
            Utils.quickSort(data, labels, col);

            double[] x = new double[data.length + 1];
            double[][] y = new double[data.length + 1][];

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
                y[i] = new double[]{ly, lyy, i, ry, ryy, data.length - i};
            }

            res[col] = new StepFunction(x, y);
        }

        return res;
    }

    public static StepFunction calculateVariance(double[] data, double[] labels) {
        double[] x = new double[data.length + 1];
        double[][] y = new double[data.length + 1][];

        Utils.quickSort(data, labels);

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
                x[i + 1] = data[i];
            y[i] = new double[]{ly, lyy, i, ry, ryy, data.length - i};
        }

        return new StepFunction(x, y);
    }
}
