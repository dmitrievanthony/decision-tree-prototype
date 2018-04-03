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

    public static List<Rectangle> toRectangles(Node node, double xFrom, double xTo, double yFrom, double yTo) {
        List<Rectangle> res = new ArrayList<>();

        if (node instanceof LeafNode) {
            LeafNode lf = (LeafNode) node;
            res.add(new Rectangle(xFrom, yFrom, xTo - xFrom, yTo - yFrom, lf.getVal()));
        }
        else {
            ConditionalNode cn = (ConditionalNode) node;
            if (cn.getCol() == 0) {
                res.addAll(toRectangles(cn.getThenNode(), cn.getThreshold(), xTo, yFrom, yTo));
                res.addAll(toRectangles(cn.getElseNode(), xFrom, cn.getThreshold(), yFrom, yTo));
            }
            else {
                res.addAll(toRectangles(cn.getThenNode(), xFrom, xTo, cn.getThreshold(), yTo));
                res.addAll(toRectangles(cn.getElseNode(), xFrom, xTo, yFrom, cn.getThreshold()));
            }
        }

        return res;
    }

    public static class Rectangle {

        private final double x;

        private final double y;

        private final double width;

        private final double height;

        private final double label;

        public Rectangle(double x, double y, double width, double height, double label) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.label = label;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getLabel() {
            return label;
        }

        @Override public String toString() {
            return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", label=" + label +
                '}';
        }
    }
}
