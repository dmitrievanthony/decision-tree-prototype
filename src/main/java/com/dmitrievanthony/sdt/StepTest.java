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
import java.util.Random;

public class StepTest {

    public static void main(String... args) {
        int rows = 1000;
        int cols = 2;

        DataPartition single = new DataPartition(rows, cols);

        Random rnd = new Random(123);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                single.data[i][j] = rnd.nextDouble();
            single.labels[i] = rnd.nextDouble();
        }

        StepFunction[] functions = VarianceCalculator.calculateVariance(single.data, single.labels);

        System.out.println();
        System.out.println("Single node: ");
        double[][][] singleRes = VarianceCalculator.toNumeric(functions);
        printRes(singleRes);

        int numOfParts = 10;
        DataPartition[] parts = new DataPartition[numOfParts];
        for (int i = 0; i < numOfParts; i++)
            parts[i] = new DataPartition(rows / numOfParts, cols);

        for (int i = 0; i < rows; i++) {
            parts[i % numOfParts].data[i / numOfParts] = single.data[i];
            parts[i % numOfParts].labels[i / numOfParts] = single.labels[i];
        }

        System.out.println();
        System.out.println("Partitioned: ");
        StepFunction[][] partFunctions = new StepFunction[numOfParts][];
        for (int i = 0; i < numOfParts; i++) {
            StepFunction[] partFunction = VarianceCalculator.calculateVariance(parts[i].data, parts[i].labels);
            for (int j = 0; j < partFunction.length; j++)
                partFunction[j] = StepFunctionCompressor.compress(partFunction[j]);
            partFunctions[i] = partFunction;
        }
        StepFunction[] total = VarianceCalculator.add(partFunctions);

        double[][][] partRes = VarianceCalculator.toNumeric(total);
        printRes(partRes);
    }

    private static void printRes(double[][][] res) {
        for (int col = 0; col < res.length; col++) {
            System.out.println("For column : " + col);
            System.out.println("Threshold : " + Arrays.toString(res[col][0]));
            System.out.println("Variance : " + Arrays.toString(res[col][1]));
        }
    }

    private static class DataPartition {

        private final double[][] data;

        private final double[] labels;

        public DataPartition(int rows, int cols) {
            this.data = new double[rows][cols];
            this.labels = new double[rows];
        }
    }
}
