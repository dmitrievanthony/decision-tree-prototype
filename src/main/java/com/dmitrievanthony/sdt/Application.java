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

import com.dmitrievanthony.sdt.criterion.GiniCriterion;
import java.util.Random;

public class Application {
    public static void main(String... args) {
        int size = 1000;
        double[][] features = new double[size][2];
        double[] labels = new double[size];

        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            double x = rnd.nextDouble() - 0.5;
            double y = rnd.nextDouble() - 0.5;

            features[i][0] = x;
            features[i][1] = y;

            labels[i] = x * y > 0 ? 1 : 0;
        }

        DecisionTree tree = new DecisionTree<>(new GiniCriterion());
        Node node = tree.fit(features, labels);

        tree.print(node, 0);
    }
}
