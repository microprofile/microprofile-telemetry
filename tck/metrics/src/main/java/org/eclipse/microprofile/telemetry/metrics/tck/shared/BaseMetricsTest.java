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
package org.eclipse.microprofile.telemetry.metrics.tck.shared;

import org.eclipse.microprofile.telemetry.metrics.tck.exporter.InMemoryMetricExporter;
import org.eclipse.microprofile.telemetry.metrics.tck.exporter.InMemoryMetricExporterProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.autoconfigure.spi.metrics.ConfigurableMetricExporterProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;
import jakarta.inject.Inject;

public class BaseMetricsTest extends Arquillian {
    @Inject
    protected Meter sdkMeter;

    @Inject
    protected InMemoryMetricExporter metricExporter;

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(BaseMetricsTest.class,
                        BasicHttpClient.class,
                        TestUtils.class,
                        InMemoryMetricExporter.class,
                        InMemoryMetricExporterProvider.class)
                .addAsLibrary(TestLibraries.AWAITILITY_LIB)
                .addAsServiceProvider(ConfigurableMetricExporterProvider.class, InMemoryMetricExporterProvider.class)
                .addAsResource(new StringAsset(
                        "otel.sdk.disabled=false\notel.metrics.exporter=in-memory\notel.logs.exporter=none\notel.traces.exporter=none\n"
                                +
                                "otel.metric.export.interval=3000"),
                        "META-INF/microprofile-config.properties")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @BeforeMethod
    void setUp() {
        if (metricExporter != null) {
            metricExporter.reset();
        }
    }

    protected MetricData assertMetric(String name, MetricDataType type, String description, String unit) {
        MetricData metric = metricExporter.getMetricData(name).get(0);

        Assert.assertEquals(metric.getType(), type);
        Assert.assertTrue(metric.getDescription().contains(description));
        Assert.assertEquals(metric.getUnit(), unit);

        return metric;
    }

}
