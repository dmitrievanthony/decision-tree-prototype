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
import com.dmitrievanthony.tree.core.local.LocalDecisionTreeClassifier;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class LocalDecisionTreeUIApplication extends UIApplication {

    private static final LocalDecisionTreeClassifier classifier = new LocalDecisionTreeClassifier();

    public static void main(String... args) {
        JFrame f=new JFrame();
        f.setSize(size,size);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(new LocalDecisionTreeUIApplication(), BorderLayout.CENTER);
        f.setVisible(true);
    }

    @Override Node classify(double[][] x, double[] y) {
        Node tree = classifier.fit(x, y);
        return tree;
    }
}
