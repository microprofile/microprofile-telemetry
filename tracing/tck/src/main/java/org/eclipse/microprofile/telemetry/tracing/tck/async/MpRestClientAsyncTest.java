/*
 * Copyright (c) 2016-2023 Contributors to the Eclipse Foundation
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.eclipse.microprofile.telemetry.tracing.tck.async;

import java.net.URL;

import org.eclipse.microprofile.telemetry.tracing.tck.BasicHttpClient;
import org.eclipse.microprofile.telemetry.tracing.tck.ConfigAsset;
import org.eclipse.microprofile.telemetry.tracing.tck.TestLibraries;
import org.eclipse.microprofile.telemetry.tracing.tck.exporter.InMemorySpanExporter;
import org.eclipse.microprofile.telemetry.tracing.tck.exporter.InMemorySpanExporterProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSpanExporterProvider;
import jakarta.inject.Inject;

public class MpRestClientAsyncTest extends Arquillian {

    @Deployment
    public static WebArchive createDeployment() {

        ConfigAsset config = new ConfigAsset()
                .add("otel.bsp.schedule.delay", "100")
                .add("otel.sdk.disabled", "false")
                .add("otel.traces.exporter", "in-memory");

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(InMemorySpanExporter.class, InMemorySpanExporterProvider.class, BasicHttpClient.class,
                        MpRestClientAsyncTestEndpoint.class)
                .addAsLibrary(TestLibraries.AWAITILITY_LIB)
                .addAsServiceProvider(ConfigurableSpanExporterProvider.class, InMemorySpanExporterProvider.class)
                .addAsResource(config, "META-INF/microprofile-config.properties")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    private BasicHttpClient basicClient;

    @ArquillianResource
    URL url;

    public static final String TEST_PASSED = "Test Passed";

    @Inject
    private InMemorySpanExporter spanExporter;

    @BeforeMethod
    void setUp() {
        // Only want to run on server
        if (spanExporter != null) {
            spanExporter.reset();
            basicClient = new BasicHttpClient(url);
        }
    }

    @Test
    public void testIntegrationWithMpRestClient() throws Exception {
        String traceId = basicClient.getResponseMessage("/MpRestClientAsyncTestEndpoint/mpclient");
        Assert.assertEquals(TEST_PASSED,
                basicClient.getResponseMessage("/MpRestClientAsyncTestEndpoint/readspans/" + traceId));
    }

    @Test
    public void testIntegrationWithMpRestClientAsync() throws Exception {
        String traceId = basicClient.getResponseMessage("/MpRestClientAsyncTestEndpoint/mpclientasync");
        Assert.assertEquals(TEST_PASSED,
                basicClient.getResponseMessage("/MpRestClientAsyncTestEndpoint/readspans/" + traceId));
    }
}
