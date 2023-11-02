package io.opentelemetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelmetry.contrib.eventlogmixer.SpanEventProcessor;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;
class TestSpanEventsAsLogs {

  static OpenTelemetrySdk initialize(LogRecordProcessor processor, InMemorySpanExporter memorySpanExporter) {
    SdkLoggerProvider loggerProvider =
        SdkLoggerProvider.builder()
            .addLogRecordProcessor(processor)
            .build();
    return OpenTelemetrySdk.builder()
        .setTracerProvider(
            SdkTracerProvider.builder()
                .addSpanProcessor(new SpanEventProcessor(
                    SimpleSpanProcessor.create(memorySpanExporter),
                    loggerProvider.get("span-events")))
                .build()
        )
        .setLoggerProvider(loggerProvider)
        .build();
  }


  @Test
  public void testSpanEventToLog() {
    AtomicReference<ReadWriteLogRecord> seenLog = new AtomicReference<>();
    InMemorySpanExporter spanMemory = InMemorySpanExporter.create();
    OpenTelemetrySdk sdk = initialize((ctx, logR) -> seenLog.lazySet(logR), spanMemory);
    String traceId = "";
    String spanId = "";
    Span span = sdk.getTracerProvider().get("test").spanBuilder("test-span").startSpan();
    try (Scope ignored = span.makeCurrent()) {
      traceId = span.getSpanContext().getTraceId();
      spanId = span.getSpanContext().getSpanId();
      span.addEvent("test-event", Attributes.builder().put("test", "value").build());
    } finally {
      span.end();
    }

    // First make sure the event made it out the log processor.
    LogRecordData result = seenLog.get().toLogRecordData();
    assertThat(result.getAttributes())
        .containsEntry("event.name", "test-event")
        .containsEntry("test", "value");
    assertThat(result.getSpanContext().getTraceId()).isEqualTo(traceId);
    assertThat(result.getSpanContext().getSpanId()).isEqualTo(spanId);

    // Check that span does not have events.
    List<SpanData> spans = spanMemory.getFinishedSpanItems();
    assertThat(spans).hasSize(1);
    assertThat(spans.get(0))
        .hasName("test-span")
        .hasTraceId(traceId)
        .hasSpanId(spanId)
        // We still inform tracing backends event total, as they are now logs.
        .hasTotalRecordedEvents(1)
        .hasEvents(Collections.emptyList());
  }
}
