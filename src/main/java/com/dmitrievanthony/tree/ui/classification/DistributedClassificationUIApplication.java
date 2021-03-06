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

package com.dmitrievanthony.tree.ui.classification;

import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.core.distributed.DistributedDecisionTreeClassifier;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import com.dmitrievanthony.tree.core.distributed.dataset.Partition;
import com.dmitrievanthony.tree.ui.util.ControlPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class DistributedClassificationUIApplication extends ClassificationUIApplication {

    public static void main(String... args) {
        JFrame f = new JFrame();
        f.setBackground(Color.decode("#2B2B2B"));
        f.setSize(500, 620);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ControlPanel ctrlPanel = new ControlPanel();
        DistributedClassificationUIApplication application = new DistributedClassificationUIApplication();
        ctrlPanel.addListener(application);

        f.add(ctrlPanel, BorderLayout.NORTH);
        f.add(application, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    @Override Node classify(double[][] x, double[] y, int maxDeep, double minImpurityDecrease) {

        Partition part1 = new Partition(Arrays.copyOfRange(x, 0, x.length / 2), Arrays.copyOfRange(y, 0, x.length / 2));
        Partition part2 = new Partition(Arrays.copyOfRange(x, x.length / 2, x.length), Arrays.copyOfRange(y, x.length / 2, x.length));

        Set<Partition> parts = new HashSet<>();
        parts.add(part1);
        parts.add(part2);

        Dataset dataset = new Dataset(parts);

        return new DistributedDecisionTreeClassifier(maxDeep, minImpurityDecrease).fit(dataset);
    }
}
