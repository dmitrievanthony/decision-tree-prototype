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

/**
 * Gini impurity measure which is calculated the following way:
 * {@code \frac{1}{L}\sum_{i=1}^{s}l_i^2 + \frac{1}{R}\sum_{i=s+1}^{n}r_i^2}.
 */
public class GiniImpurityMeasure implements ImpurityMeasure<GiniImpurityMeasure> {
    /** Number of elements of each type in the left part. */
    private final long[] left;

    /** Number of elements of each type in the right part. */
    private final long[] right;

    /**
     * Constructs a new instance of Gini impurity measure.
     *
     * @param left Number of elements of each type in the left part.
     * @param right Number of elements of each type in the right part.
     */
    public GiniImpurityMeasure(long[] left, long[] right) {
        if (left.length != right.length)
            throw new IllegalArgumentException("Left and right parts have to be the same length");

        this.left = left;
        this.right = right;
    }

    /** {@inheritDoc} */
    @Override public double impurity() {
        long leftCnt = 0;
        long rightCnt = 0;

        double leftImpurity = 0;
        double rightImpurity = 0;

        for (long e : left) {
            leftImpurity += Math.pow(e, 2);
            leftCnt += e;
        }

        for (long e : right) {
            rightImpurity += Math.pow(e, 2);
            rightCnt += e;
        }

        double totalImpurity = 0;
        totalImpurity += leftCnt > 0 ? leftImpurity / leftCnt: 0;
        totalImpurity += rightCnt > 0 ? rightImpurity / rightCnt : 0;

        return -totalImpurity;
    }

    /** {@inheritDoc} */
    @Override public GiniImpurityMeasure add(GiniImpurityMeasure b) {
        if (left.length != b.left.length || left.length != b.right.length)
            throw new IllegalArgumentException("Added measure has to have length " + left.length);

        long[] leftRes = new long[left.length];
        long[] rightRes = new long[left.length];

        for (int i = 0; i < left.length; i++) {
            leftRes[i] = left[i] + b.left[i];
            rightRes[i] = right[i] + b.right[i];
        }

        return new GiniImpurityMeasure(leftRes, rightRes);
    }

    /** {@inheritDoc} */
    @Override public GiniImpurityMeasure subtract(GiniImpurityMeasure b) {
        if (left.length != b.left.length || left.length != b.right.length)
            throw new IllegalStateException("Subtracted measure has to have length " + left.length);

        long[] leftRes = new long[left.length];
        long[] rightRes = new long[left.length];

        for (int i = 0; i < left.length; i++) {
            leftRes[i] = left[i] - b.left[i];
            rightRes[i] = right[i] - b.right[i];
        }

        return new GiniImpurityMeasure(leftRes, rightRes);
    }
}
