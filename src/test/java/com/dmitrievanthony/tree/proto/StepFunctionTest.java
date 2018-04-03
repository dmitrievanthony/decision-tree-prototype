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

package com.dmitrievanthony.tree.proto;

import com.dmitrievanthony.tree.core.distributed.criteria.ImpurityMeasure;
import com.dmitrievanthony.tree.core.distributed.criteria.StepFunction;
import java.util.Objects;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class StepFunctionTest {

    @Test
    public void testAddIncreasing() {
        StepFunction<FuncResult> a = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2),
            createY(0, 1, 2)
        );
        StepFunction<FuncResult> b = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1.5, 2.5),
            createY(0, 1, 2)
        );

        StepFunction<FuncResult> c = a.add(b);

        assertArrayEquals(createX(Double.NEGATIVE_INFINITY, 1, 1.5, 2, 2.5), c.getX(), 1e-12);
        assertArrayEquals(createY(0, 1, 2, 3, 4), c.getY());
    }

    @Test
    public void testAddDecreasing() {
        StepFunction<FuncResult> a = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2),
            createY(2, 1, 0)
        );
        StepFunction<FuncResult> b = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1.5, 2.5),
            createY(2, 1, 0)
        );

        StepFunction<FuncResult> c = a.add(b);

        assertArrayEquals(createX(Double.NEGATIVE_INFINITY, 1, 1.5, 2, 2.5), c.getX(), 1e-12);
        assertArrayEquals(createY(4, 3, 2, 1, 0), c.getY());
    }

    @Test
    public void testAddSamePointDecreasing() {
        StepFunction<FuncResult> a = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2),
            createY(2, 1, 0)
        );
        StepFunction<FuncResult> b = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2.5),
            createY(2, 1, 0)
        );

        StepFunction<FuncResult> c = a.add(b);

        assertArrayEquals(createX(Double.NEGATIVE_INFINITY, 1, 2, 2.5), c.getX(), 1e-12);
        assertArrayEquals(createY(4, 2, 1, 0), c.getY());
    }

    @Test
    public void testAddSamePointIncreasing() {
        StepFunction<FuncResult> a = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2),
            createY(0, 1, 2)
        );
        StepFunction<FuncResult> b = new StepFunction<>(
            createX(Double.NEGATIVE_INFINITY, 1, 2.5),
            createY(0, 1, 2)
        );

        StepFunction<FuncResult> c = a.add(b);

        assertArrayEquals(createX(Double.NEGATIVE_INFINITY, 1, 2, 2.5), c.getX(), 1e-12);
        assertArrayEquals(createY(0, 2, 3, 4), c.getY());
    }

    private static double[] createX(double... x) {
        return x;
    }

    private static FuncResult[] createY(double... y) {
        FuncResult[] results = new FuncResult[y.length];

        for (int i = 0; i < y.length; i++)
            results[i] = new FuncResult(y[i]);

        return results;
    }

    private static class FuncResult implements ImpurityMeasure<FuncResult> {

        private final double val;

        public FuncResult(double val) {
            this.val = val;
        }

        @Override public double impurity() {
            return val;
        }

        @Override public FuncResult add(FuncResult b) {
            return new FuncResult(val + b.val);
        }

        @Override public FuncResult subtract(FuncResult b) {
            return new FuncResult(val - b.val);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            FuncResult res = (FuncResult)o;

            return Double.compare(res.val, val) == 0;
        }

        @Override public int hashCode() {

            return Objects.hash(val);
        }
    }
}
