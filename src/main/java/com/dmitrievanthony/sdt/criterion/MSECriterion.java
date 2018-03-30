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

package com.dmitrievanthony.sdt.criterion;

public class MSECriterion implements Criterion<Double> {

    @Override public Double calculate(double[] labels, int lSize, Double prevVal) {
        double sumL = 0;
        double sumR = 0;
        for (int i = 0; i < lSize; i++)
            sumL += labels[i];
        for (int i = lSize; i < labels.length; i++)
            sumR += labels[i];
        double meanL = sumL / lSize;
        double meanR = sumR / (labels.length - lSize);

        double resL = 0;
        double resR = 0;
        for (int i = 0; i < lSize; i++)
            resL += Math.pow(labels[i] - meanL, 2);
        for (int i = lSize; i < labels.length; i++)
            resR += Math.pow(labels[i] - meanR, 2);

        return resL / lSize + resR / (labels.length - lSize);
    }
}
