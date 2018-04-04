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

package com.dmitrievanthony.tree.core.local.criteria;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MSESplittingCriteriaTest {

    private final MSESplitCalculator criteria = new MSESplitCalculator();

    @Test
    public void testFindBestSplitWithTwoClasses() {
        double[] labels = new double[] {0, 0, 0, 1, 1, 1};

        SplitPoint pnt = criteria.findBestSplit(labels, 0);

        assertEquals(3, pnt.getLeftSize());
        assertEquals(0.0, pnt.getImpurityVal(), 1e-6);
    }

    @Test
    public void testFindBestSplitWithThreeClasses() {
        double[] labels = new double[] {0, 0, 0, 1, 1, 2, 1, 2, 2};

        SplitPoint pnt = criteria.findBestSplit(labels, 0);

        assertEquals(3, pnt.getLeftSize());
        assertEquals(0.25, pnt.getImpurityVal(), 1e-6);
    }

    @Test
    public void testFindBestSplitOnSingleElementArray() {
        double[] labels = new double[] {1};

        SplitPoint pnt = criteria.findBestSplit(labels, 0);

        assertEquals(0, pnt.getLeftSize());
        assertEquals(0, pnt.getImpurityVal(), 1e-6);
    }

    @Test
    public void testFindBestSplitOnEmptyArray() {
        double[] labels = new double[] {};

        SplitPoint pnt = criteria.findBestSplit(labels, 0);

        assertEquals(null, pnt);
    }
}
