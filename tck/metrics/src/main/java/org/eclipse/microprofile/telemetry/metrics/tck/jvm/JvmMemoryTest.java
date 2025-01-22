/*
 **********************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/
package org.eclipse.microprofile.telemetry.metrics.tck.jvm;

import java.io.IOException;

import org.eclipse.microprofile.telemetry.metrics.tck.shared.BaseMetricsTest;
import org.testng.annotations.Test;

import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class JvmMemoryTest extends BaseMetricsTest {
    @Test
    void testJvmMemoryUsedMetric() throws IOException {
        assertMetric("jvm.memory.used", MetricDataType.LONG_SUM,
                "Measure of memory used.", "By");
    }

    @Test
    void testJvmMemoryCommittedMetric() throws IOException {
        assertMetric("jvm.memory.committed", MetricDataType.LONG_SUM,
                "Measure of memory committed.", "By");
    }

    @Test
    void testMemoryLimitMetric() throws IOException {
        assertMetric("jvm.memory.limit", MetricDataType.LONG_SUM,
                "Measure of max obtainable memory.", "By");
    }

    @Test
    void testMemoryUsedAfterLastGcMetric() throws IOException {
        assertMetric("jvm.memory.used_after_last_gc", MetricDataType.LONG_SUM,
                "Measure of memory used, as measured after the most recent garbage collection event on this pool.",
                "By");
    }

}
