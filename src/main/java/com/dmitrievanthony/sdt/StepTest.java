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

import java.util.Arrays;


public class StepTest {

    public static void main(String... args) {
        double[] data = new double[]{0, 1, 2, 3};
        double[] labels = new double[]{0, 1, 2, 3};

        StepFunction function = calculateVariance(data, labels);

        double[] d1 = new double[]{0, 2};
        double[] l1 = new double[]{0, 2};

        double[] d2 = new double[]{1, 3};
        double[] l2 = new double[]{1, 3};

        System.out.println("Single node: ");
        System.out.println(Arrays.deepToString(function.y));
        System.out.println();

        System.out.println("Partitioned: ");
        StepFunction a = calculateVariance(d1, l1);
        StepFunction b = calculateVariance(d2, l2);
        StepFunction c = a.add(b);
        System.out.println(Arrays.deepToString(c.y));
    }

    private static StepFunction calculateVariance(double[] data, double[] labels) {
        double[] x = new double[data.length + 1];
        double[][] y = new double[data.length + 1][];

        quickSort(data, labels);

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

    private static class StepFunction {

        private final double[] x;

        private final double[][] y;

        public StepFunction(double[] x, double[][] y) {
            this.x = x;
            this.y = y;
        }

        public StepFunction add(StepFunction b) {
            quickSort(x, y);
            quickSort(b.x, b.y);

            int size = 0;

            int l = 0, r = 0;
            double last = 0;
            while (l < x.length || r < b.x.length) {
                if (r >= b.x.length || (l < x.length && x[l] < b.x[r])) {
                    if (size == 0 || x[l] != last) {
                        last = x[l];
                        size++;
                    }
                    l++;
                }
                else {
                    if (size == 0 || b.x[r] != last) {
                        last = b.x[r];
                        size++;
                    }
                    r++;
                }
            }

            double[] resX = new double[size];
            double[][] resY = new double[size][];

            l = 0;
            r = 0;
            for (int i = 0; l < x.length || r < b.x.length; i++) {
                if (r >= b.x.length || (l < x.length && x[l] < b.x[r])) {
                    boolean override = i > 0 && x[l] == resX[i - 1];
                    int target = override ? i - 1 : i;
                    resX[target] = x[l];
                    resY[target] = override ? resY[target] : new double[y[l].length];

                    for (int j = 0; j < y[l].length; j++)
                        resY[target][j] = (i > 0 ? resY[i - 1][j] : 0) + y[l][j] - (l > 0 ? y[l - 1][j] : 0);
                    i = target;
                    l++;
                }
                else {
                    boolean override = i > 0 && b.x[r] == resX[i - 1];
                    int target = override ? i - 1 : i;
                    resX[target] = b.x[r];
                    resY[target] = override ? resY[target] : new double[b.y[r].length];

                    for (int j = 0; j < b.y[r].length; j++)
                        resY[target][j] = (i > 0 ? resY[i - 1][j] : 0) + b.y[r][j] - (r > 0 ? b.y[r - 1][j] : 0);
                    i = target;
                    r++;
                }
            }

            return new StepFunction(resX, resY);
        }
    }

    static void quickSort(double[] x, double[][] y) {
        quickSort(x, y, 0, x.length - 1);
    }

    static private void quickSort(double[] x, double[][] y, int from, int to) {
        if (from < to) {
            double pivot = x[(from + to) / 2];
            int i = from, j = to;
            while (i <= j) {
                while (x[i] < pivot) i++;
                while (x[j] > pivot) j--;
                if (i <= j) {
                    double tmpX = x[i];
                    x[i] = x[j];
                    x[j] = tmpX;
                    double[] tmpY = y[i];
                    y[i] = y[j];
                    y[j] = tmpY;
                    i++;
                    j--;
                }
            }
            quickSort(x, y, from, j);
            quickSort(x, y, i, to);
        }
    }

    static void quickSort(double[] x, double[] y) {
        quickSort(x, y, 0, x.length - 1);
    }

    static private void quickSort(double[] x, double[] y, int from, int to) {
        if (from < to) {
            double pivot = x[(from + to) / 2];
            int i = from, j = to;
            while (i <= j) {
                while (x[i] < pivot) i++;
                while (x[j] > pivot) j--;
                if (i <= j) {
                    double tmpX = x[i];
                    x[i] = x[j];
                    x[j] = tmpX;
                    double tmpY = y[i];
                    y[i] = y[j];
                    y[j] = tmpY;
                    i++;
                    j--;
                }
            }
            quickSort(x, y, from, j);
            quickSort(x, y, i, to);
        }
    }
}
