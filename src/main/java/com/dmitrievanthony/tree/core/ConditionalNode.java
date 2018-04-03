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

package com.dmitrievanthony.tree.core;

public class ConditionalNode implements Node {

    private final int col;

    private final double threshold;

    private final Node thenNode;

    private final Node elseNode;

    public ConditionalNode(int col, double threshold, Node thenNode, Node elseNode) {
        this.col = col;
        this.threshold = threshold;
        this.thenNode = thenNode;
        this.elseNode = elseNode;
    }

    @Override public double predict(double[] features) {
        return features[col] > threshold ? thenNode.predict(features) : elseNode.predict(features);
    }

    public int getCol() {
        return col;
    }

    public double getThreshold() {
        return threshold;
    }

    public Node getThenNode() {
        return thenNode;
    }

    public Node getElseNode() {
        return elseNode;
    }
}
