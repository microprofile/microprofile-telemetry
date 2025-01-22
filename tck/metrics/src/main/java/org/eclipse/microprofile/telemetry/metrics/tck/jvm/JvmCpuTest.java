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

import org.eclipse.microprofile.telemetry.metrics.tck.shared.BaseMetricsTest;
import org.testng.annotations.Test;

import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class JvmCpuTest extends BaseMetricsTest {
    @Test
    void testCpuTimeMetric() {
        assertMetric("jvm.cpu.time", MetricDataType.DOUBLE_SUM,
                "CPU time used by the process as reported by the JVM.", "s");
    }

    @Test
    void testCpuCountMetric() {
        assertMetric("jvm.cpu.count", MetricDataType.LONG_SUM,
                "Number of processors available to the Java virtual machine.", "{cpu}");
    }

    @Test
    void testCpuRecentUtilizationMetric() {
        assertMetric("jvm.cpu.recent_utilization", MetricDataType.DOUBLE_GAUGE,
                "Recent CPU utilization for the process as reported by the JVM.", "1");
    }
}
