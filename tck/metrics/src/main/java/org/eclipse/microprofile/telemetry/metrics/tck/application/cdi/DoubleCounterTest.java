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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.telemetry.metrics.tck.shared.BaseMetricsTest;
import org.eclipse.microprofile.telemetry.metrics.tck.shared.TestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class DoubleCounterTest extends BaseMetricsTest {
    private static final String COUNTER_NAME = "testDoubleCounter";
    private static final String COUNTER_DESCRIPTION = "Testing double counter";
    private static final String COUNTER_UNIT = "Metric Tonnes";

    private static final double DOUBLE_WITH_ATTRIBUTES = 20.2;
    private static final double DOUBLE_WITHOUT_ATTRIBUTES = 10.1;

    @Test
    void testDoubleCounter() {
        DoubleCounter doubleCounter =
                sdkMeter
                        .counterBuilder(COUNTER_NAME)
                        .ofDoubles()
                        .setDescription(COUNTER_DESCRIPTION)
                        .setUnit(COUNTER_UNIT)
                        .build();
        Assert.assertNotNull(doubleCounter);

        Map<Double, Attributes> expectedResults = new HashMap<Double, Attributes>();
        expectedResults.put(DOUBLE_WITH_ATTRIBUTES, Attributes.builder().put("K", "V").build());
        expectedResults.put(DOUBLE_WITHOUT_ATTRIBUTES, Attributes.empty());

        expectedResults.keySet().stream().forEach(key -> doubleCounter.add(key, expectedResults.get(key)));

        List<MetricData> metrics = metricExporter.getMetricData((COUNTER_NAME));
        metrics.stream()
                .peek(metricData -> {
                    Assert.assertEquals(metricData.getType(), MetricDataType.DOUBLE_SUM);
                    Assert.assertEquals(metricData.getDescription(), COUNTER_DESCRIPTION);
                    Assert.assertEquals(metricData.getUnit(), COUNTER_UNIT);
                })
                .flatMap(metricData -> metricData.getDoubleSumData().getPoints().stream())
                .forEach(point -> {
                    Assert.assertTrue(expectedResults.containsKey(point.getValue()),
                            "Double" + point.getValue() + " was not an expected result");
                    Assert.assertTrue(point.getAttributes().equals(expectedResults.get(point.getValue())),
                            "Attributes were not equal."
                                    + System.lineSeparator() + "Actual values: "
                                    + TestUtils.mapToString(point.getAttributes().asMap())
                                    + System.lineSeparator() + "Expected values: "
                                    + TestUtils.mapToString(expectedResults.get(point.getValue()).asMap()));
                });
    }

}
