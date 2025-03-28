/*
 * Copyright (c) 2022-2023 Contributors to the Eclipse Foundation
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

package org.eclipse.microprofile.telemetry.tracing.tck.exporter;

import static io.opentelemetry.semconv.HttpAttributes.HTTP_ROUTE;
import static io.opentelemetry.semconv.UrlAttributes.URL_QUERY;
import static java.util.Comparator.comparingLong;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.testng.Assert;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InMemorySpanExporter implements SpanExporter {
    private boolean isStopped = false;
    private final List<SpanData> finishedSpanItems = new CopyOnWriteArrayList<>();

    /**
     * Careful when retrieving the list of finished spans. There is a chance when the response is already sent to the
     * client and the server still writing the end of the spans. This means that a response is available to assert from
     * the test side but not all spans may be available yet. For this reason, this method requires the number of
     * expected spans.
     */
    public List<SpanData> getFinishedSpanItems(int spanCount) {
        assertSpanCount(spanCount);
        return finishedSpanItems.stream().sorted(comparingLong(SpanData::getStartEpochNanos).reversed())
                .collect(Collectors.toList());
    }

    public void assertSpanCount(int spanCount) {
        Awaitility.await().pollDelay(3, SECONDS).atMost(10, SECONDS)
                .untilAsserted(() -> Assert.assertEquals(finishedSpanItems.size(), spanCount));
    }

    public void reset() {
        finishedSpanItems.clear();
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        if (isStopped) {
            return CompletableResultCode.ofFailure();
        }
        spans.stream()
                .filter(not(InMemorySpanExporter::isArquillianSpan))
                .forEach(finishedSpanItems::add);
        return CompletableResultCode.ofSuccess();
    }

    private static boolean isArquillianSpan(SpanData span) {
        String path = span.getAttributes().get(HTTP_ROUTE);
        if (path == null) {
            path = span.getAttributes().get(URL_QUERY);
        }
        if (path != null
                && (path.contains("/ArquillianServletRunner")
                        || path.contains("/ArquillianRESTRunnerEE9")
                        || path.contains("/ArquillianServletRunnerEE9"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        finishedSpanItems.clear();
        isStopped = true;
        return CompletableResultCode.ofSuccess();
    }

    public SpanData getFirst(SpanKind spanKind) {
        return finishedSpanItems.stream().filter(span -> span.getKind() == spanKind).findFirst()
                .orElseThrow(() -> new IllegalStateException("No span found with kind " + spanKind));
    }

    public List<SpanData> getSpansWithKind(SpanKind spanKind) {
        return finishedSpanItems.stream().filter(span -> span.getKind() == spanKind).collect(Collectors.toList());
    }
}
