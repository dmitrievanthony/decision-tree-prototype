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

/**
 * Split calculator used MSE (variance) impurity measure which is calculated the following way:
 * {@code \frac{1}{L}\sum_{i=0}^{n}(y_i - \mu)^2}.
 */
public class MSESplitCalculator implements SplitCalculator {
    /** {@inheritDoc} */
    @Override public SplitPoint findBestSplit(double[] labels, double minImpurityDecrease) {
        SplitPoint bestSplitPnt = null;
        double initImpurity = 0;

        for (int leftSize = 0; leftSize < labels.length; leftSize++) {
            double sumL = 0;
            double sumR = 0;
            for (int i = 0; i < leftSize; i++)
                sumL += labels[i] / leftSize;
            for (int i = leftSize; i < labels.length; i++)
                sumR += labels[i] / (labels.length - leftSize);
            double meanL = sumL;
            double meanR = sumR;

            double resL = 0;
            double resR = 0;
            for (int i = 0; i < leftSize; i++)
                resL += (Math.pow(labels[i] - meanL, 2) / leftSize);
            for (int i = leftSize; i < labels.length; i++)
                resR += (Math.pow(labels[i] - meanR, 2) / (labels.length - leftSize));

            double mse = resR + resL;

            if (leftSize == 0)
                initImpurity = mse;
            else
                if ((bestSplitPnt == null || mse < bestSplitPnt.getImpurityVal() && (initImpurity - mse) > minImpurityDecrease))
                    bestSplitPnt = new SplitPoint(leftSize, mse);
        }

        return bestSplitPnt;
    }
}
