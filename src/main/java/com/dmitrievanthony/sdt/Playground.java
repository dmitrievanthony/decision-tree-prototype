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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Playground {

//    public static void main(String... args) {
//        int size = 4;
//
//        // Create common dataset.
//        double[] data = new double[size];
//        double[] labels = new double[size];
//
//        // Create first partition dataset.
//        double[] data1 = new double[size / 2];
//        double[] labels1 = new double[size / 2];
//
//        // Create second partition dataset.
//        double[] data2 = new double[size / 2];
//        double[] labels2 = new double[size / 2];
//
//        Random rnd = new Random(0);
//        for (int i = 0; i < size; i++) {
//            double x = rnd.nextDouble();
//            double y = rnd.nextDouble();
//            data[i] = x;
//            labels[i] = y;
//            if (i < size / 2) {
//                data1[i] = x;
//                labels1[i] = y;
//            }
//            else {
//                data2[i - size / 2] = x;
//                labels2[i - size / 2] = y;
//            }
//        }
//
//        System.out.println("Data : " + Arrays.toString(data));
//        System.out.println("Labels : " + Arrays.toString(labels));
//
//        System.out.println("Part 1 Data : " + Arrays.toString(data1));
//        System.out.println("Part 1 Labels : " + Arrays.toString(labels1));
//
//        System.out.println("Part 2 Data : " + Arrays.toString(data2));
//        System.out.println("Part 2 Labels : " + Arrays.toString(labels2));
//        System.out.println();
//
//        Map<Double, double[]> var1 = calculateVariance(data, labels, 0);
//        Map<Double, double[]> var2 = calculateVariance(data1, labels1, data2, labels2);
//
//        System.out.println("Single node calculation: ");
//        List<Double> list1 = new ArrayList<>(var1.keySet());
//        Collections.sort(list1);
//        for (Double e : list1)
//            System.out.println(e + " -> " + Arrays.toString(var1.get(e)));
//
//        System.out.println();
//
//        System.out.println("Partitioned calculation: ");
//        List<Double> list2 = new ArrayList<>(var2.keySet());
//        Collections.sort(list2);
//        for (double e : list2)
//            System.out.println(e + " -> " + Arrays.toString(var2.get(e)));
//    }
//
//    private static Map<Double, double[]> calculateVariance(double[] data, double[] labels) {
//        Map<Double, double[]> res = new HashMap<>();
//
//        quickSort(data, labels);
//        for (int i = 0; i <= data.length; i++) {
//            double ly = 0;
//            double lyy = 0;
//            double ry = 0;
//            double ryy = 0;
//
//            for (int j = 0; j < i; j++) {
//                ly += labels[j];
//                lyy += Math.pow(labels[j], 2);
//            }
//
//            for (int j = i; j < labels.length; j++) {
//                ry += labels[j];
//                ryy += Math.pow(labels[j], 2);
//            }
//
//            res.put(i == data.length ? Double.MAX_VALUE : data[i], new double[]{ly, ry});
//        }
//
//        return res;
//    }
//
//    private static Map<Double, double[]> calculateVariance(double[] data1, double[] labels1, double[] data2, double[] labels2) {
//        Map<Double, double[]> firstPartVariance = calculateVariance(data1, labels1, 0);
//        Map<Double, double[]> secondPartVariance = calculateVariance(data2, labels2, 0);
//
//        System.out.println("    First partition:");
//        List<Double> list1 = new ArrayList<>(firstPartVariance.keySet());
//        Collections.sort(list1);
//        for (Double e : list1)
//            System.out.println("    " + e + " -> " + Arrays.toString(firstPartVariance.get(e)));
//
//        System.out.println();
//
//        System.out.println("    Second partition:");
//        List<Double> list2 = new ArrayList<>(secondPartVariance.keySet());
//        Collections.sort(list2);
//        for (Double e : list2)
//            System.out.println("    " + e + " -> " + Arrays.toString(secondPartVariance.get(e)));
//
//        System.out.println();
//
//
//        double[] firstSplitPoints = new double[firstPartVariance.size()];
//        double[] secondSplitPoints = new double[secondPartVariance.size()];
//
//        int ptr = 0;
//        for (Double splitPoint : firstPartVariance.keySet())
//            firstSplitPoints[ptr++] = splitPoint;
//
//        ptr = 0;
//        for (Double splitPoint : secondPartVariance.keySet())
//            secondSplitPoints[ptr++] = splitPoint;
//
//        Arrays.sort(firstSplitPoints);
//        Arrays.sort(secondSplitPoints);
//
//        Map<Double, double[]> totalRes = new HashMap<>();
//        Double lastPnt = null;
//        int firstPtr = 0, secondPtr = 0;
//        for (int i = 0; i < (firstSplitPoints.length + secondSplitPoints.length); i++) {
//            if (firstPtr >= firstSplitPoints.length) {
//                double[] diff = diff(secondSplitPoints, secondPartVariance, secondPtr);
////                System.out.println("Take from the second partition* (" + secondSplitPoints[l] + "), diff: " + Arrays.toString(diff));
//
//                System.out.println("1 : first ptr = " + firstPtr + ", second ptr = " + secondPtr);
//
//                if (i > 0) {
//                    if (lastPnt != null) {
//                        double[] prevArr = totalRes.get(lastPnt);
//                        diff = applyDiff(prevArr, diff);
//                    }
//                }
//
//                totalRes.put(secondSplitPoints[secondPtr], diff);
//                lastPnt = secondSplitPoints[secondPtr];
//                secondPtr++;
//            }
//            else if (secondPtr >= secondSplitPoints.length) {
//                double[] diff = diff(firstSplitPoints, firstPartVariance, firstPtr);
////                System.out.println("Take from the first partition* (" + firstSplitPoints[k] + "), diff: " + Arrays.toString(diff));
//
//                System.out.println("2 : first ptr = " + firstPtr + ", second ptr = " + secondPtr);
//
//                if (i > 0) {
//                    if (lastPnt != null) {
//                        double[] prevArr = totalRes.get(lastPnt);
//                        diff = applyDiff(prevArr, diff);
//                    }
//                }
//
//                totalRes.put(firstSplitPoints[firstPtr], diff);
//                lastPnt = firstSplitPoints[firstPtr];
//                firstPtr++;
//            }
//            else if ((firstSplitPoints[firstPtr] >= secondSplitPoints[secondPtr] && secondPtr != 0) || firstPtr == 0) {
//                double[] diff = diff(secondSplitPoints, secondPartVariance, secondPtr);
////                System.out.println("Take from the second partition (" + secondSplitPoints[l] + "), diff: " + Arrays.toString(diff));
//
//                System.out.println("3 : first ptr = " + firstPtr + ", second ptr = " + secondPtr);
//
//                if (i > 0) {
//                    if (lastPnt != null) {
//                        double[] prevArr = totalRes.get(lastPnt);
//                        diff = applyDiff(prevArr, diff);
//                    }
//                }
//
//                totalRes.put(secondSplitPoints[secondPtr], diff);
//                lastPnt = secondSplitPoints[secondPtr];
//                secondPtr++;
//            }
//            else {
//                double[] diff = diff(firstSplitPoints, firstPartVariance, firstPtr);
////                System.out.println("Take from the first partition (" + firstSplitPoints[k] + "), diff: " + Arrays.toString(diff));
//
//                System.out.println("4 : first ptr = " + firstPtr + ", second ptr = " + secondPtr);
//
//                if (i > 0) {
//                    if (lastPnt != null) {
//                        double[] prevArr = totalRes.get(lastPnt);
//                        diff = applyDiff(prevArr, diff);
//                    }
//                }
//
//                totalRes.put(firstSplitPoints[firstPtr], diff);
//                lastPnt = firstSplitPoints[firstPtr];
//                firstPtr++;
//            }
//        }
//
//        return totalRes;
//    }
//
//    private static double[] diff(double[] points, Map<Double, double[]> variance, int ptr) {
//        double pnt = points[ptr];
//        double[] var = variance.get(pnt);
//        if (ptr > 0) {
//            double prevPnt = points[ptr - 1];
//            double[] prevVar = variance.get(prevPnt);
//            for (int i = 0; i < var.length; i++)
//                var[i] = var[i] - prevVar[i];
//        }
//        return var;
//    }
//
//    private static double[] applyDiff(double[] prev, double[] diff) {
//        double[] res = new double[prev.length];
//        for (int i = 0; i < prev.length; i++)
//            res[i] = prev[i] + diff[i];
//        return res;
//    }
//
//    static void quickSort(double[] features, double[] labels) {
//        quickSort(features, labels, 0, features.length - 1);
//    }
//
//    private static void quickSort(double[] features, double[] labels, int from, int to) {
//        if (from < to) {
//            double pivot = features[(from + to) / 2];
//            int i = from, j = to;
//            while (i <= j) {
//                while (features[i] < pivot) i++;
//                while (features[j] > pivot) j--;
//                if (i <= j) {
//                    double tmpFeatures = features[i];
//                    features[i] = features[j];
//                    features[j] = tmpFeatures;
//                    double tmpLb = labels[i];
//                    labels[i] = labels[j];
//                    labels[j] = tmpLb;
//                    i++;
//                    j--;
//                }
//            }
//            quickSort(features, labels, from, j);
//            quickSort(features, labels, i, to);
//        }
//    }
}
