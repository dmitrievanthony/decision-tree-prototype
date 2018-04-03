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

package com.dmitrievanthony.tree.utils;

import com.dmitrievanthony.tree.core.ConditionalNode;
import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.Node;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <T> void quickSort(double[] x, T[] y) {
        quickSort(x, y, 0, x.length - 1);
    }

    public static void quickSort(double[] x, double[] y) {
        quickSort(x, y, 0, x.length - 1);
    }

    public static void quickSort(double[][] x, double[] y, int col) {
        quickSort(x, y, col, 0, x.length - 1);
    }

    public static void print(Node node, int deep) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < deep; i++)
            builder.append("  ");
        if (node instanceof LeafNode) {
            builder.append("return ").append(((LeafNode)node).getVal()).append(";");
            System.out.println(builder.toString());
        }
        else if (node instanceof ConditionalNode) {
            ConditionalNode conditionalNode = (ConditionalNode) node;
            System.out.println(builder.toString() + "if (f[" + conditionalNode.getCol()+ "] > " + conditionalNode.getThreshold()+ ") {");
            print(conditionalNode.getThenNode(), deep + 1);
            System.out.println(builder.toString() + "} else {");
            print(conditionalNode.getElseNode(), deep + 1);
            System.out.println(builder.toString() + "}");
        }
        else
            throw new IllegalStateException();
    }

    private static <T> void quickSort(double[] x, T[] y, int from, int to) {
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
                    T tmpY = y[i];
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

    private static void quickSort(double[] x, double[] y, int from, int to) {
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

    private static void quickSort(double[][] x, double[] y, int col, int from, int to) {
        if (from < to) {
            double pivot = x[(from + to) / 2][col];
            int i = from, j = to;
            while (i <= j) {
                while (x[i][col] < pivot) i++;
                while (x[j][col] > pivot) j--;
                if (i <= j) {
                    double[] tmpX = x[i];
                    x[i] = x[j];
                    x[j] = tmpX;
                    double tmpY = y[i];
                    y[i] = y[j];
                    y[j] = tmpY;
                    i++;
                    j--;
                }
            }
            quickSort(x, y, col, from, j);
            quickSort(x, y, col, i, to);
        }
    }
}
