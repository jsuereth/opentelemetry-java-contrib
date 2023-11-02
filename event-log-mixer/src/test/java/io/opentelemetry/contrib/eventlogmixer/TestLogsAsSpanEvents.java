package io.opentelemetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelmetry.contrib.eventlogmixer.LogEventProcessor;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;
class TestLogsAsSpanEvents {
  static OpenTelemetrySdk initialize(
      LogRecordProcessor processor, InMemorySpanExporter memorySpanExporter) {
    SdkLoggerProvider loggerProvider =
        SdkLoggerProvider.builder()
            .addLogRecordProcessor(
                new LogEventProcessor(processor))
            .build();
    return OpenTelemetrySdk.builder()
        .setTracerProvider(
            SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(memorySpanExporter))
                .build()
        )
        .setLoggerProvider(loggerProvider)
        .build();
  }

  @Test
  void testLogToSpanEvent() {
    CopyOnWriteArrayList<ReadWriteLogRecord> logMemory = new CopyOnWriteArrayList<>();
    InMemorySpanExporter spanMemory = InMemorySpanExporter.create();
    OpenTelemetrySdk sdk = initialize((ctx, logR) -> logMemory.add(logR), spanMemory);
    // Create a span and remember the id.
    String traceId = "";
    String spanId = "";
    Span span = sdk.getTracerProvider().get("test").spanBuilder("test-span").startSpan();
    try (Scope ignored = span.makeCurrent()) {
      traceId = span.getSpanContext().getTraceId();
      spanId = span.getSpanContext().getSpanId();
      // Write the log.
      sdk.getLogsBridge().get("test").logRecordBuilder()
          .setContext(Context.current())
          .setAttribute(AttributeKey.stringKey("event.name"), "test-event")
          .setAttribute(AttributeKey.stringKey("test"), "value")
          .emit();
    } finally {
      span.end();
    }

    // First check we don't have any logs.
    // assertThat(logMemory).isEmpty();

    // Next check the spans.
    List<SpanData> spans = spanMemory.getFinishedSpanItems();
    assertThat(spans).hasSize(1);
    assertThat(spans.get(0))
        .hasName("test-span")
        .hasTraceId(traceId)
        .hasSpanId(spanId)
        // We still inform tracing backends event total, as they are now logs.
        .hasTotalRecordedEvents(1)
        .hasEventsSatisfying(events -> {
          assertThat(events).hasSize(1);
          assertThat(events.get(0)).hasName("test-event");
          assertThat(events.get(0)).hasAttributes(
              Attributes.builder()
                  .put("test","value")
                  .put("body", "")
                  .build()
          );
        });

  }

}
