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
import java.lang.reflect.Array;
import java.util.Arrays;

public class StepFunction<T extends ImpurityMeasure<T>> {

    private final double[] x;

    private final T[] y;

    public StepFunction(double[] x, T[] y) {
        this.x = x;
        this.y = y;
    }

    public StepFunction<T> add(StepFunction<T> b) {
        Utils.quickSort(x, y);
        Utils.quickSort(b.x, b.y);

        int size = 0;

        int l = 0, r = 0;
        double last = 0;
        while (l < x.length || r < b.x.length) {
            if (r >= b.x.length || (l < x.length && x[l] < b.x[r])) {
                if (size == 0 || x[l] != last) {
                    last = x[l];
                    size++;
                }
                l++;
            }
            else {
                if (size == 0 || b.x[r] != last) {
                    last = b.x[r];
                    size++;
                }
                r++;
            }
        }

        double[] resX = new double[size];
        T[] resY = Arrays.copyOf(y, size);

        l = 0;
        r = 0;
        for (int i = 0; l < x.length || r < b.x.length; i++) {
            if (r >= b.x.length || (l < x.length && x[l] < b.x[r])) {
                boolean override = i > 0 && x[l] == resX[i - 1];
                int target = override ? i - 1 : i;

                resY[target] = override ? resY[target] : null;
                resY[target] = i > 0 ? resY[i - 1] : null;
                resY[target] = resY[target] == null ? y[l] : resY[target].add(y[l]);
                if (l > 0)
                    resY[target] = resY[target].subtract(y[l - 1]);

                resX[target] = x[l];
                i = target;
                l++;
            }
            else {
                boolean override = i > 0 && b.x[r] == resX[i - 1];
                int target = override ? i - 1 : i;
                resY[target] = override ? resY[target] : null;
                resY[target] = i > 0 ? resY[i - 1] : null;
                resY[target] = resY[target] == null ? b.y[r] : resY[target].add(y[r]);
                if (r > 0)
                    resY[target] = resY[target].subtract(b.y[r - 1]);
                resX[target] = b.x[r];
                i = target;
                r++;
            }
        }

        return new StepFunction<>(resX, resY);
    }

    public double[] getX() {
        return x;
    }

    public T[] getY() {
        return y;
    }
}
