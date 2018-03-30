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
import com.dmitrievanthony.sdt.criterion.MSECriterion;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DecisionTreeTest {

    private final DecisionTree classificationTree = new DecisionTree<>(new GiniCriterion());

    private final DecisionTree regressionTree = new DecisionTree<>(new MSECriterion());

    @Test
    public void testQuickSort() {
        double[][] x = new double[][]{{1}, {4}, {2}, {7}, {3}, {1}, {0}, {9}};
        double[] y = new double[]{1, 2, 3, 4, 5, 1, 7, 8};

        classificationTree.quickSort(x, y, 0);

        assertArrayEquals(new double[][]{{0}, {1}, {1}, {2}, {3}, {4}, {7}, {9}}, x);
        assertArrayEquals(new double[]{7, 1, 1, 3, 5, 2, 4, 8}, y, 1e-5);
    }

    @Test
    public void testClassificationFit() {
        double[][] features = new double[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        double[] labels = new double[]{1, 0, 1, 0};

        Node mdl = classificationTree.fit(features, labels);

        assertEquals(1.0, mdl.predict(new double[]{1, 1}), 1e-5);
        assertEquals(0.0, mdl.predict(new double[]{1, -1}), 1e-5);
        assertEquals(1.0, mdl.predict(new double[]{-1, -1}), 1e-5);
        assertEquals(0.0, mdl.predict(new double[]{-1, 1}), 1e-5);
    }
}
