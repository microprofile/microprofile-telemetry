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
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class LongHistogramTest extends BaseMetricsTest {
    private static final String HISTOGRAM_NAME = "testLongHistogram";
    private static final String HISTOGRAM_DESCRIPTION = "Testing long histogram";
    private static final String HISTOGRAM_UNIT = "Metric Tonnes";

    private static final long LONG_WITH_ATTRIBUTES = 20;
    private static final long LONG_WITHOUT_ATTRIBUTES = 10;

    @Test
    void testLongHistogram() {
        LongHistogram longHistogram =
                sdkMeter
                        .histogramBuilder(HISTOGRAM_NAME)
                        .ofLongs()
                        .setDescription(HISTOGRAM_DESCRIPTION)
                        .setUnit(HISTOGRAM_UNIT)
                        .build();
        Assert.assertNotNull(longHistogram);

        Map<Long, Attributes> expectedResults = new HashMap<Long, Attributes>();
        expectedResults.put(LONG_WITH_ATTRIBUTES, Attributes.builder().put("K", "V").build());
        expectedResults.put(LONG_WITHOUT_ATTRIBUTES, Attributes.empty());

        expectedResults.keySet().stream().forEach(key -> longHistogram.record(key, expectedResults.get(key)));

        List<MetricData> metrics = metricExporter.getMetricData((HISTOGRAM_NAME));
        metrics.stream()
                .peek(metricData -> {
                    Assert.assertEquals(metricData.getType(), MetricDataType.HISTOGRAM);
                    Assert.assertEquals(metricData.getDescription(), HISTOGRAM_DESCRIPTION);
                    Assert.assertEquals(metricData.getUnit(), HISTOGRAM_UNIT);
                })
                .flatMap(metricData -> metricData.getHistogramData().getPoints().stream())
                .forEach(point -> {
                    Assert.assertTrue(expectedResults.containsKey((long) point.getSum()),
                            "Long " + (long) point.getSum() + " was not an expected result");
                    Assert.assertTrue(point.getAttributes().equals(expectedResults.get((long) point.getSum())),
                            "Attributes were not equal."
                                    + System.lineSeparator() + "Actual values: "
                                    + TestUtils.mapToString(point.getAttributes().asMap())
                                    + System.lineSeparator() + "Expected values: "
                                    + TestUtils.mapToString(expectedResults.get((long) point.getSum()).asMap()));
                });
    }
}
