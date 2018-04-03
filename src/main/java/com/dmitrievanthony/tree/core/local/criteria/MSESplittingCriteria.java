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

public class MSESplittingCriteria implements SplittingCriteria {

    @Override public SplitPoint findBestSplit(double[] labels) {
        SplitPoint bestSplitPnt = null;

        for (int leftSize = 0; leftSize < labels.length; leftSize++) {
            double sumL = 0;
            double sumR = 0;
            for (int i = 0; i < leftSize; i++)
                sumL += labels[i];
            for (int i = leftSize; i < labels.length; i++)
                sumR += labels[i];
            double meanL = sumL / leftSize;
            double meanR = sumR / (labels.length - leftSize);

            double resL = 0;
            double resR = 0;
            for (int i = 0; i < leftSize; i++)
                resL += Math.pow(labels[i] - meanL, 2);
            for (int i = leftSize; i < labels.length; i++)
                resR += Math.pow(labels[i] - meanR, 2);

            double mse = resR / (labels.length - leftSize);
            if (leftSize != 0)
                mse += resL / leftSize;

            if (bestSplitPnt == null || mse < bestSplitPnt.getCriteriaVal())
                bestSplitPnt = new SplitPoint(leftSize, mse);
        }

        return bestSplitPnt;
    }
}
