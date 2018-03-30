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

import com.dmitrievanthony.sdt.criterion.Criterion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DecisionTree<T extends Comparable<T>> {

    private final double PROBABILITY_THRESHOLD = 0.8;

    private final int DEEP_THRESHOLD = 5;

    private final Criterion<T> criterion;

    public DecisionTree(Criterion<T> criterion) {
        this.criterion = criterion;
    }

    public Node fit(double[][] features, double[] labels) {
        return fit(features, labels, 0);
    }

    private Node fit(double[][] features, double[] labels, int deep) {
        if (isStop(labels, deep))
            return createLeafNode(labels);

        T bestCriterionVal = null;
        int bestCol = -1;
        int bestLSize = -1;

        for (int col = 0; col < features[0].length; col++) {
            quickSort(features, labels, col);
            T criterionVal = null;
            for (int lSize = 0; lSize < features.length; lSize++) {
                if (lSize > 0 && features[lSize][col] == features[lSize - 1][col])
                    continue;

                criterionVal = criterion.calculate(labels, lSize, criterionVal);
                if (bestCriterionVal == null || criterionVal.compareTo(bestCriterionVal) <= 0) {
                    bestCriterionVal = criterionVal;
                    bestCol = col;
                    bestLSize = lSize;
                }
            }
        }

        quickSort(features, labels, bestCol);

        double[][] leftFeatures = Arrays.copyOfRange(features, 0, bestLSize);
        double[][] rightFeatures = Arrays.copyOfRange(features, bestLSize, features.length);

        double[] leftLabels = Arrays.copyOfRange(labels, 0, bestLSize);
        double[] rightLabels = Arrays.copyOfRange(labels, bestLSize, labels.length);

        return new ConditionalNode(
            bestCol,
            (leftFeatures[leftFeatures.length - 1][bestCol] + rightFeatures[0][bestCol]) / 2,
            fit(rightFeatures, rightLabels, deep + 1),
            fit(leftFeatures, leftLabels, deep + 1)
        );
    }

    private boolean isStop(double[] labels, int deep) {
        // Check deep threshold.
        if (deep >= DEEP_THRESHOLD)
            return true;

        // Check probability threshold.
        Map<Double, Integer> cnt = new HashMap<>();
        for (double label : labels) {
            if (cnt.containsKey(label))
                cnt.put(label, cnt.get(label) + 1);
            else
                cnt.put(label, 1);
        }

        for (Map.Entry<Double, Integer> e : cnt.entrySet())
            if (1.0 * e.getValue() / labels.length >= PROBABILITY_THRESHOLD)
                return true;

        return false;
    }

    private LeafNode createLeafNode(double[] labels) {
        int maxCnt = 0;
        double res = 0;

        Map<Double, Integer> cnt = new HashMap<>();
        for (double label : labels) {
            if (cnt.containsKey(label))
                cnt.put(label, cnt.get(label) + 1);
            else
                cnt.put(label, 1);
        }

        for (Map.Entry<Double, Integer> e : cnt.entrySet()) {
            if (e.getValue() > maxCnt) {
                maxCnt = e.getValue();
                res = e.getKey();
            }
        }

        return new LeafNode(res);
    }

    public String toStr(double[][] arr) {
        StringBuilder builder = new StringBuilder();
        for (double[] e : arr)
            builder.append(Arrays.toString(e)).append(" ");
        return builder.toString();
    }

    public void print(Node node, int deep) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < deep; i++)
            builder.append("  ");
        if (node instanceof LeafNode) {
            builder.append("return ").append(((LeafNode)node).getVal()).append(";");
            System.out.println(builder.toString());
        }
        else if (node instanceof ConditionalNode) {
            ConditionalNode conditionalNode = (ConditionalNode) node;
            System.out.println(builder.toString() + "if (f[" + conditionalNode.getCol()+ "] >= " + conditionalNode.getThreshold()+ ") {");
            print(conditionalNode.getThenNode(), deep + 1);
            System.out.println(builder.toString() + "} else {");
            print(conditionalNode.getElseNode(), deep + 1);
            System.out.println(builder.toString() + "}");
        }
        else
            throw new IllegalStateException();
    }

    void quickSort(double[][] features, double[] labels, int col) {
        quickSort(features, labels, col, 0, features.length - 1);
    }

    private void quickSort(double[][] features, double[] labels, int col, int from, int to) {
        if (from < to) {
            double[] pivot = features[(from + to) / 2];
            int i = from, j = to;
            while (i <= j) {
                while (features[i][col] < pivot[col]) i++;
                while (features[j][col] > pivot[col]) j--;
                if (i <= j) {
                    double[] tmpFeatures = features[i];
                    features[i] = features[j];
                    features[j] = tmpFeatures;
                    double tmpLb = labels[i];
                    labels[i] = labels[j];
                    labels[j] = tmpLb;
                    i++;
                    j--;
                }
            }
            quickSort(features, labels, col, from, j);
            quickSort(features, labels, col, i, to);
        }
    }
}
