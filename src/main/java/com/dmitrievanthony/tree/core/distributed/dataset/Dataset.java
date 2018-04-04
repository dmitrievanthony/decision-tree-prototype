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

package com.dmitrievanthony.tree.core.distributed.dataset;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Dataset that consists of partitions.
 */
public class Dataset {
    /** Set of partitions. */
    private final Set<Partition> partitions;

    /**
     * Constructs a new instance of dataset.
     *
     * @param partitions Set of partitions.
     */
    public Dataset(Set<Partition> partitions) {
        this.partitions = partitions;
    }

    /**
     * Performs the specified {@code mapper} function of every partition and then reduces results using the specified
     * {@code reducer} function.
     *
     * @param mapper Mapper function applied on every partition.
     * @param reducer Reducer used to reduce results from different partitions.
     * @param <R> Type of return value.
     * @return Result.
     */
    public <R> R compute(Function<Partition, R> mapper, BiFunction<R, R, R> reducer) {
        R res = null;

        for (Partition part : partitions) {
            R partRes = mapper.apply(part);
            res = reducer.apply(res, partRes);
        }

        return res;
    }
}
