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

package com.dmitrievanthony.tree.core.distributed.criteria;

import com.dmitrievanthony.tree.utils.Utils;
import java.util.Arrays;

/**
 * Step function described by {@code x} and {@code y} points.
 *
 * @param <T>
 */
public class StepFunction<T extends ImpurityMeasure<T>> {
    /** Argument of every steps start. Should be ascendingly sorted all the time. */
    private final double[] x;

    /** Value of every step. */
    private final T[] y;

    /**
     * Constructs a new instance of step function.
     *
     * @param x Argument of every steps start.
     * @param y Value of every step.
     */
    public StepFunction(double[] x, T[] y) {
        if (x.length != y.length)
            throw new IllegalArgumentException("Argument and value arrays have to be the same length");

        this.x = x;
        this.y = y;

        Utils.quickSort(x, y);
    }

    /**
     * Adds the given step function to this.
     *
     * @param b Another step function.
     * @return Sum of this and the given function.
     */
    public StepFunction<T> add(StepFunction<T> b) {
        int resSize = 0, leftPtr = 0, rightPtr = 0;
        double previousPnt = 0;

        while (leftPtr < x.length || rightPtr < b.x.length) {
            if (rightPtr >= b.x.length || (leftPtr < x.length && x[leftPtr] < b.x[rightPtr])) {
                if (resSize == 0 || x[leftPtr] != previousPnt) {
                    previousPnt = x[leftPtr];
                    resSize++;
                }

                leftPtr++;
            }
            else {
                if (resSize == 0 || b.x[rightPtr] != previousPnt) {
                    previousPnt = b.x[rightPtr];
                    resSize++;
                }

                rightPtr++;
            }
        }

        double[] resX = new double[resSize];
        T[] resY = Arrays.copyOf(y, resSize);

        leftPtr = 0;
        rightPtr = 0;

        for (int i = 0; leftPtr < x.length || rightPtr < b.x.length; i++) {
            if (rightPtr >= b.x.length || (leftPtr < x.length && x[leftPtr] < b.x[rightPtr])) {
                boolean override = i > 0 && x[leftPtr] == resX[i - 1];
                int target = override ? i - 1 : i;

                resY[target] = override ? resY[target] : null;
                resY[target] = i > 0 ? resY[i - 1] : null;
                resY[target] = resY[target] == null ? y[leftPtr] : resY[target].add(y[leftPtr]);

                if (leftPtr > 0)
                    resY[target] = resY[target].subtract(y[leftPtr - 1]);

                resX[target] = x[leftPtr];
                i = target;
                leftPtr++;
            }
            else {
                boolean override = i > 0 && b.x[rightPtr] == resX[i - 1];
                int target = override ? i - 1 : i;

                resY[target] = override ? resY[target] : null;
                resY[target] = i > 0 ? resY[i - 1] : null;
                resY[target] = resY[target] == null ? b.y[rightPtr] : resY[target].add(y[rightPtr]);

                if (rightPtr > 0)
                    resY[target] = resY[target].subtract(b.y[rightPtr - 1]);

                resX[target] = b.x[rightPtr];
                i = target;
                rightPtr++;
            }
        }

        return new StepFunction<>(resX, resY);
    }

    /**
     * Returns argument of every steps start.
     *
     * @return Argument of every steps start.
     */
    public double[] getX() {
        return x;
    }

    /**
     * Returns value of every step.
     *
     * @return Value of every step.
     */
    public T[] getY() {
        return y;
    }
}
