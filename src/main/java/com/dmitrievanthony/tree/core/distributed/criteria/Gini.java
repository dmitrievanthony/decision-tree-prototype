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

import com.dmitrievanthony.tree.core.distributed.util.WithAdd;
import com.dmitrievanthony.tree.core.distributed.util.WithSubtract;

public class Gini implements Comparable<Gini>, WithAdd<Gini>, WithSubtract<Gini> {

    private final long[] left;

    private final long[] right;

    public Gini(long[] left, long[] right) {
        this.left = left;
        this.right = right;
    }

    @Override public Gini add(Gini b) {
        if (left.length != right.length || left.length != b.left.length || left.length != b.right.length)
            throw new IllegalStateException();

        long[] leftRes = new long[left.length];
        long[] rightRes = new long[left.length];

        for (int i = 0; i < left.length; i++) {
            leftRes[i] = left[i] + b.left[i];
            rightRes[i] = right[i] + b.right[i];
        }

        return new Gini(leftRes, rightRes);
    }

    @Override public Gini subtract(Gini b) {
        if (left.length != right.length || left.length != b.left.length || left.length != b.right.length)
            throw new IllegalStateException();

        long[] leftRes = new long[left.length];
        long[] rightRes = new long[left.length];

        for (int i = 0; i < left.length; i++) {
            leftRes[i] = left[i] - b.left[i];
            rightRes[i] = right[i] - b.right[i];
        }

        return new Gini(leftRes, rightRes);
    }

    @Override public int compareTo(Gini o) {
        return Double.compare(calculate(), o.calculate());
    }

    private double calculate() {
        long leftCnt = 0;
        long rightCnt = 0;

        double leftRes = 0;
        double rightRes = 0;

        for (int i = 0; i < left.length; i++) {
            leftRes += Math.pow(left[i], 2);
            leftCnt += left[i];
        }

        for (int i = 0; i < right.length; i++) {
            rightRes += Math.pow(right[i], 2);
            rightCnt += right[i];
        }

        return leftRes / leftCnt + rightRes / rightCnt;
    }

    @Override public String toString() {
        return "Gini{" +
            calculate() +
            '}';
    }
}
