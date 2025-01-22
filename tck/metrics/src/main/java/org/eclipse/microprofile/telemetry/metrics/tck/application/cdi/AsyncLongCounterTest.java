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
package org.eclipse.microprofile.telemetry.metrics.tck.application.cdi;

import org.eclipse.microprofile.telemetry.metrics.tck.shared.BaseMetricsTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class AsyncLongCounterTest extends BaseMetricsTest {

    private static final String COUNTER_NAME = "testAsyncLongCounter";
    private static final String COUNTER_DESCRIPTION = "Testing long counter";
    private static final String COUNTER_UNIT = "Metric Tonnes";

    @Test
    void testAsyncLongCounter() {
        Assert.assertNotNull(
                sdkMeter
                        .counterBuilder(COUNTER_NAME)
                        .setDescription(COUNTER_DESCRIPTION)
                        .setUnit(COUNTER_UNIT)
                        .buildWithCallback(measurement -> {
                            measurement.record(1, Attributes.empty());
                        }));

        MetricData metric = assertMetric(COUNTER_NAME, MetricDataType.LONG_SUM, COUNTER_DESCRIPTION, COUNTER_UNIT);

        Assert.assertEquals(metric.getLongSumData()
                .getPoints()
                .stream()
                .findFirst()
                .get()
                .getValue(), 1);
    }
}
