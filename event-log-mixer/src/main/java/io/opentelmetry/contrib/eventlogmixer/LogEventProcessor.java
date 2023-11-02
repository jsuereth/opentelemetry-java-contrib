package io.opentelmetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.ReadWriteLogRecord;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import java.util.concurrent.TimeUnit;

/** Converts log records into span events, when there is an active span in context. */
public class LogEventProcessor implements LogRecordProcessor {
  private final LogRecordProcessor delegate;

  public LogEventProcessor(LogRecordProcessor delegate) {this.delegate = delegate;}

  @Override
  public void onEmit(Context context, ReadWriteLogRecord logRecord) {
    Span span = Span.fromContext(context);
    if (!span.isRecording()) {
      delegate.onEmit(context, logRecord);
      return;
    }
    // We have a legit span, let's record on it:
    LogRecordData logData = logRecord.toLogRecordData();
    logData.getBody();
    String eventName = logData.getAttributes().get(AttributeKey.stringKey("event.name"));
    // TODO - Should we include other LogRecord top-level fields?
    Attributes eventAttributes = Attributes.builder()
            .putAll(logData.getAttributes())
                .put(AttributeKey.stringKey("log.body"), logData.getBody().asString())
                    .build();
    span.addEvent(eventName, eventAttributes, logData.getTimestampEpochNanos(), TimeUnit.NANOSECONDS);
  }
}
