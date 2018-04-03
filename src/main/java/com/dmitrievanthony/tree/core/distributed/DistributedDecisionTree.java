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

package com.dmitrievanthony.tree.core.distributed;

import com.dmitrievanthony.tree.core.ConditionalNode;
import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import com.dmitrievanthony.tree.core.distributed.criteria.SplittingCriteria;
import com.dmitrievanthony.tree.core.distributed.util.StepFunction;
import com.dmitrievanthony.tree.core.distributed.util.WithAdd;
import com.dmitrievanthony.tree.core.distributed.util.WithSubtract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class DistributedDecisionTree<T extends Comparable<T> & WithAdd<T> & WithSubtract<T>> {

    private final SplittingCriteria<T> criterionCalculator;

    private final int maxDeep;

    private final double minImpurityDecrease;

    private final Class<T> clazz;

    public DistributedDecisionTree(SplittingCriteria<T> criterionCalculator, int maxDeep, double minImpurityDecrease, Class<T> clazz) {
        this.criterionCalculator = criterionCalculator;
        this.maxDeep = maxDeep;
        this.minImpurityDecrease = minImpurityDecrease;
        this.clazz = clazz;
    }

    public Node fit(Dataset dataset) {
        return split(dataset, e -> true, 0);
    }

    @SuppressWarnings("unchecked")
    private Node split(Dataset dataset, Predicate<double[]> pred, int deep) {
        LeafNode ln = createLeafNode(dataset, pred);

        if (ln != null)
            return ln;

        StepFunction<T>[] sf = dataset.compute(part -> {
            List<double[]> filteredFeatures = new ArrayList<>();
            List<Double> filteredLabels = new ArrayList<>();

            for (int i = 0; i < part.getLabels().length; i++) {
                double[] f = part.getFeatures()[i];
                if (pred.test(f)) {
                    double l = part.getLabels()[i];
                    filteredFeatures.add(f);
                    filteredLabels.add(l);
                }
            }

            double[][] ff = new double[filteredFeatures.size()][];
            double[] fl = new double[filteredLabels.size()];

            for (int i = 0; i < filteredLabels.size(); i++) {
                ff[i] = filteredFeatures.get(i);
                fl[i] = filteredLabels.get(i);
            }

            if (ff.length == 0)
                return null;
            else
                return criterionCalculator.calculate(ff, fl);

        }, this::reduce);

        T bestVar = null;
        double bestThreshold = 0;
        int bestCol = 0;

        for (int i = 0; i < sf.length; i++) {
            StepFunction<T> fff = sf[i];

            for (int j = 1; j < fff.getY().length - 1; j++) {
                T v = fff.getY()[j];
                if (bestVar == null || v.compareTo(bestVar) > 0) {
                    bestVar = v;
                    bestCol = i;
                    bestThreshold = (fff.getX()[j] + fff.getX()[j + 1]) / 2.0;
                }
            }
        }

        int finalBestCol = bestCol;
        double finalBestThreshold = bestThreshold;

        return new ConditionalNode(
            bestCol,
            bestThreshold,
            split(dataset, pred.and(f -> {
                boolean r = f[finalBestCol] > finalBestThreshold;
//                System.out.println("Test object " + Arrays.toString(f) + " f[" + finalBestCol + "] >= " + finalBestThreshold + ", answer is " + r);
                return r;
            }), deep + 1),
            split(dataset, pred.and(f -> {
                boolean r = f[finalBestCol] <= finalBestThreshold;
//                System.out.println("Test object " + Arrays.toString(f) + " f[" + finalBestCol + "] < " + finalBestThreshold + ", answer is " + r);
                return r;
            }), deep + 1)
        );
    }

    private LeafNode createLeafNode(Dataset dataset, Predicate<double[]> pred) {
        Map<Double, Integer> r = dataset.compute(part -> {
            Map<Double, Integer> cntr = new HashMap<>();

            for (int i = 0; i < part.getLabels().length; i++) {
                if (pred.test(part.getFeatures()[i])) {
                    double lb = part.getLabels()[i];
                    if (cntr.containsKey(lb))
                        cntr.put(lb, cntr.get(lb) + 1);
                    else
                        cntr.put(lb, 1);
                }
            }

            return cntr;
        }, (a, b) -> {
            if (a != null) {
                for (Map.Entry<Double, Integer> e : b.entrySet()) {
                    if (a.containsKey(e.getKey()))
                        b.put(e.getKey(), e.getValue() + a.get(e.getKey()));
                }
            }
            return b;
        });

        double maxVal = 0;
        int maxCnt = 0;
        int totalCnt = 0;

        for (Map.Entry<Double, Integer> e : r.entrySet()) {
            if (e.getValue() > maxCnt) {
                maxVal = e.getKey();
                maxCnt = e.getValue();
            }
            totalCnt += e.getValue();
        }

        if (maxCnt == totalCnt)
            return new LeafNode(maxVal);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    private StepFunction<T>[] reduce(StepFunction<T>[] a, StepFunction<T>[] b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        else {
            StepFunction<T>[] res = new StepFunction[a.length];
            for (int i = 0; i < a.length; i++)
                res[i] = a[i].add(b[i]);
            return res;
        }
    }

    abstract Optional<LeafNode> createLeafNode(Dataset dataset, Predicate<double[]> pred, int deep);
}
