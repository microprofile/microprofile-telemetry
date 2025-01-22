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
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.telemetry.metrics.tck.shared.BaseMetricsTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.opentelemetry.sdk.metrics.data.MetricDataType;

public class JvmGarbageCollectionTest extends BaseMetricsTest {
    @Test
    void testGarbageCollectionCountMetric() throws IOException {
        waitForGarbageCollection();

        assertMetric("jvm.gc.duration", MetricDataType.HISTOGRAM,
                "Duration of JVM garbage collection actions.", "s");
    }

    // returns true if the GC was invoked, otherwise false;
    private void waitForGarbageCollection() {
        long startTime = System.nanoTime();

        // This unusual bit of code is to give the gc something to clean up, with methods
        // We can call so the JIT cannot short circuit.
        String garbageString = new String("garbage");
        WeakReference<String> weakRef = new WeakReference<String>(garbageString);
        garbageString = null;

        for (int i = 0; i < 10; ++i) {
            System.gc();
            // give the GC some time to actually do its thing

            String fromWeakRef = weakRef.get();

            // If our WeakReference returned null we know the garbage collector has acted
            if (fromWeakRef == null) {
                return;
            } else {
                // Do something with the weak string to be absolutely sure
                // The JIT doesn't optimise it away
                String alsoIgnored = fromWeakRef.strip();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long stopTime = System.nanoTime();
        long seconds = TimeUnit.SECONDS.toSeconds(stopTime - startTime);

        Assert.fail("The garbage collector did not run after " + seconds + "seconds");

    }

}
