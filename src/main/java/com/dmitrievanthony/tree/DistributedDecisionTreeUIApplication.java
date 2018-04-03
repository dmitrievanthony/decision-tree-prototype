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

package com.dmitrievanthony.tree;

import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.core.distributed.DistributedDecisionTreeClassifier;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import com.dmitrievanthony.tree.core.distributed.dataset.Partition;
import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class DistributedDecisionTreeUIApplication extends UIApplication {

    private static final DistributedDecisionTreeClassifier classifier = new DistributedDecisionTreeClassifier();

    public static void main(String... args) {
        JFrame f=new JFrame();
        f.setSize(size,size);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(new DistributedDecisionTreeUIApplication(), BorderLayout.CENTER);
        f.setVisible(true);
    }

    @Override Node classify(double[][] x, double[] y) {

        Partition part = new Partition(x, y);

        Set<Partition> parts = new HashSet<>();
        parts.add(part);

        Dataset dataset = new Dataset(parts);

        return classifier.fit(dataset);
    }
}
