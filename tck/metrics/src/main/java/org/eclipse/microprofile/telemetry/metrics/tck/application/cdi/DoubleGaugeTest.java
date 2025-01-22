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

public class DoubleGaugeTest extends BaseMetricsTest {
    private static final String GAUGE_NAME = "testDoubleGauge";
    private static final String GAUGE_DESCRIPTION = "Testing double gauge";
    private static final String GAUGE_UNIT = "ms";

    @Test
    void testDoubleGauge() throws InterruptedException {
        Assert.assertNotNull(
                sdkMeter
                        .gaugeBuilder(GAUGE_NAME)
                        .setDescription(GAUGE_DESCRIPTION)
                        .setUnit("ms")
                        .buildWithCallback(measurement -> {
                            measurement.record(1, Attributes.empty());
                        }));

        MetricData metric = assertMetric(GAUGE_NAME, MetricDataType.DOUBLE_GAUGE, GAUGE_DESCRIPTION, GAUGE_UNIT);

        Assert.assertEquals(metric.getDoubleGaugeData()
                .getPoints()
                .stream()
                .findFirst()
                .get()
                .getValue(), 1);
    }

}
