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

package com.dmitrievanthony.tree.ui.util;

import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class ControlPanel extends JPanel {

    private static final int DEFAULT_MAX_DEEP = 2;

    private static final int MAX_MAX_DEEP = 20;

    private static final double DEFAULT_MIN_IMPURITY_DECREASE = 0;

    private static final double MAX_MIN_IMPURITY_DECREASE = 10;

    private List<ControlPanelListener> listeners = new CopyOnWriteArrayList<>();

    public ControlPanel() {
        setBorder(BorderFactory.createEtchedBorder());

        setPreferredSize(new Dimension(500, 120));

        add(createMaxDeepControlPanel());
        add(createMinImpurityDecreaseControlPanel());
        add(createCleanControlPanel());
    }

    private JPanel createMaxDeepControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 30));

        JLabel lb = new JLabel("Max deep: ");
        panel.add(lb);

        JSlider slider = new JSlider();
        slider.setValue(DEFAULT_MAX_DEEP);
        slider.setMaximum(MAX_MAX_DEEP);
        panel.add(slider);

        slider.addChangeListener(e -> listeners.forEach(
            listener -> listener.doOnMaxDeepChange(slider.getValue())
        ));

        return panel;
    }

    private JPanel createMinImpurityDecreaseControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 30));

        JLabel lb = new JLabel("Min impurity decrease: ");
        panel.add(lb);

        JSlider slider = new JSlider();
        slider.setValue((int) (DEFAULT_MIN_IMPURITY_DECREASE * 100));
        slider.setMaximum((int) (MAX_MIN_IMPURITY_DECREASE * 100));
        panel.add(slider);

        slider.addChangeListener(e -> listeners.forEach(
            listener -> listener.doOnMinImpurityDecreaseChange(slider.getValue() / 100.0)
        ));

        return panel;
    }

    private JPanel createCleanControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 30));

        JButton btn = new JButton("Clean");
        panel.add(btn);

        btn.addActionListener(e -> listeners.forEach(ControlPanelListener::doOnClean));

        return panel;
    }

    public void addListener(ControlPanelListener lsnr) {
        listeners.add(lsnr);
    }

    public void removeListener(ControlPanelListener lsnr) {
        listeners.remove(lsnr);
    }
}
