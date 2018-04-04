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

package com.dmitrievanthony.tree.ui.regression;

import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.core.distributed.DistributedDecisionTreeRegressor;
import com.dmitrievanthony.tree.core.distributed.dataset.Dataset;
import com.dmitrievanthony.tree.core.distributed.dataset.Partition;
import com.dmitrievanthony.tree.ui.util.ControlPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class DistributedRegressionUIApplication extends RegressionUIApplication {

    public static void main(String... args) {
        JFrame f = new JFrame();
        f.setSize(500, 650);
        f.setBackground(Color.decode("#2B2B2B"));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ControlPanel ctrlPanel = new ControlPanel();
        DistributedRegressionUIApplication application = new DistributedRegressionUIApplication();
        ctrlPanel.addListener(application);

        f.add(ctrlPanel, BorderLayout.NORTH);
        f.add(application, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    @Override Node regress(double[][] x, double[] y, int maxDeep, double minImpurityDecrease) {
        Partition part = new Partition(x, y);

        Set<Partition> parts = new HashSet<>();
        parts.add(part);

        Dataset dataset = new Dataset(parts);

        return new DistributedDecisionTreeRegressor(maxDeep, minImpurityDecrease).fit(dataset);
    }
}
