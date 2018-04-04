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
import com.dmitrievanthony.tree.core.local.LocalDecisionTreeRegressor;
import com.dmitrievanthony.tree.ui.util.ControlPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class LocalRegressionUIApplication extends RegressionUIApplication {

    public static void main(String... args) {
        JFrame f = new JFrame();
        f.setSize(500, 650);
        f.setBackground(Color.decode("#2B2B2B"));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ControlPanel ctrlPanel = new ControlPanel();
        LocalRegressionUIApplication application = new LocalRegressionUIApplication();
        ctrlPanel.addListener(application);

        f.add(ctrlPanel, BorderLayout.NORTH);
        f.add(application, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    @Override Node regress(double[][] x, double[] y, int maxDeep, double minImpurityDecrease) {
        return new LocalDecisionTreeRegressor(maxDeep, minImpurityDecrease).fit(x, y);
    }
}
