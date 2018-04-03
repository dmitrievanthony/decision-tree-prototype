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

public class MSEImpurityMeasure implements ImpurityMeasure<MSEImpurityMeasure> {

    private final double leftY;

    private final double leftY2;

    private final long leftCnt;

    private final double rightY;

    private final double rightY2;

    private final long rightCnt;

    public MSEImpurityMeasure(double leftY, double leftY2, long leftCnt, double rightY, double rightY2, long rightCnt) {
        this.leftY = leftY;
        this.leftY2 = leftY2;
        this.leftCnt = leftCnt;
        this.rightY = rightY;
        this.rightY2 = rightY2;
        this.rightCnt = rightCnt;
    }

    @Override public double impurity() {
        double left = leftY2 - 2.0 * leftY / leftCnt * leftY + Math.pow(leftY / leftCnt, 2) * leftCnt;
        double right = rightY2 - 2.0 * rightY / rightCnt * rightY + Math.pow(rightY / rightCnt, 2) * rightCnt;

        return left + right;
    }

    @Override public MSEImpurityMeasure add(MSEImpurityMeasure b) {
        return new MSEImpurityMeasure(
            leftY + b.leftY,
            leftY2 + b.leftY2,
            leftCnt + b.leftCnt,
            rightY + b.rightY,
            rightY2 + b.rightY2,
            rightCnt + b.rightCnt
        );
    }

    @Override public MSEImpurityMeasure subtract(MSEImpurityMeasure b) {
        return new MSEImpurityMeasure(
            leftY - b.leftY,
            leftY2 - b.leftY2,
            leftCnt - b.leftCnt,
            rightY - b.rightY,
            rightY2 - b.rightY2,
            rightCnt - b.rightCnt
        );
    }
}