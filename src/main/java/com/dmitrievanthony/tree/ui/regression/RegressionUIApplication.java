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

import com.dmitrievanthony.tree.core.ConditionalNode;
import com.dmitrievanthony.tree.core.LeafNode;
import com.dmitrievanthony.tree.core.Node;
import com.dmitrievanthony.tree.ui.util.ControlPanelListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class RegressionUIApplication extends JPanel implements ControlPanelListener {

    private final List<Point> points = new ArrayList<>();

    private List<Line> lines;

    private int maxDeep = 2;

    private double minImpurityDecrease = 0;

    public RegressionUIApplication() {
        setBorder(BorderFactory.createEtchedBorder());

        setPreferredSize(new Dimension(500, 500));

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                update();
            }
        });
    }

    abstract Node regress(double[][] x, double[] y, int maxDeep, double minImpurityDecrease);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        for (Point pnt : points) {
            g.setColor(Color.GRAY);
            g.fillOval(pnt.x - 2, pnt.y - 2, 4, 4);
        }

        if (lines != null) {
            Line prevLine = null;
            for (Line line: lines) {
                g.setColor(Color.BLUE);
                if (prevLine != null)
                    g.drawLine(prevLine.x2, prevLine.y2, line.x1, line.y1);

                g.drawLine(line.x1, line.y1, line.x2, line.y2);
                prevLine = line;
            }
        }
    }

    private void update() {
        double[][] x = new double[points.size()][];
        double[] y = new double[points.size()];

        for (int i = 0; i < points.size(); i++) {
            Point pnt = points.get(i);
            x[i] = new double[] {pnt.x};
            y[i] = pnt.y;
        }

        Node tree = regress(x, y, maxDeep, minImpurityDecrease);
        lines = toLines(tree, 0, 500);
        lines.sort(Comparator.comparingInt(l -> l.x1));

        repaint();
    }

    private List<Line> toLines(Node node, int left, int right) {
        List<Line> lines = new ArrayList<>();

        if (node instanceof LeafNode) {
            LeafNode lf = (LeafNode) node;
            lines.add(new Line(left, (int) lf.getVal(), right, (int) lf.getVal()));
        }
        else if (node instanceof ConditionalNode) {
            ConditionalNode cn = (ConditionalNode) node;
            lines.addAll(toLines(cn.getThenNode(), (int) cn.getThreshold(), right));
            lines.addAll(toLines(cn.getElseNode(), left, (int) cn.getThreshold()));
        }

        return lines;
    }

    @Override public void doOnMaxDeepChange(int maxDeep) {
        this.maxDeep = maxDeep;
        update();
    }

    @Override public void doOnMinImpurityDecreaseChange(double minImpurityDecrease) {
        this.minImpurityDecrease = minImpurityDecrease;
        update();
    }

    @Override public void doOnClean() {
        points.clear();
        update();
    }

    private static class Line {

        private final int x1;

        private final int y1;

        private final int x2;

        private final int y2;

        public Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
