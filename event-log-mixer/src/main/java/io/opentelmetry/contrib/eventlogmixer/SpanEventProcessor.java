package io.opentelmetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.api.logs.Logger;
import java.util.concurrent.TimeUnit;

public class SpanEventProcessor implements SpanProcessor {
  private final SpanProcessor delegate;
  private final Logger logger;

  public SpanEventProcessor(SpanProcessor delegate, Logger logger) {
    this.delegate = delegate;
    this.logger = logger;
  }

  @Override
  public void onStart(Context parentContext, ReadWriteSpan span) {
    delegate.onStart(parentContext, span);
  }

  @Override
  public boolean isStartRequired() {
    return delegate.isStartRequired();
  }

  @Override
  public void onEnd(ReadableSpan span) {
    // First, fire events out logs
    for (EventData event : span.toSpanData().getEvents()) {
      logger.logRecordBuilder()
          .setTimestamp(event.getEpochNanos(), TimeUnit.NANOSECONDS)
          .setAllAttributes(event.getAttributes())
          // TODO - use semconv
          .setAttribute(AttributeKey.stringKey("event.name"), event.getName())
          .setContext(Context.root().with(Span.wrap(span.getSpanContext())))
          .emit();
    }
    // Then fire the span to our processor.
    delegate.onEnd(new EventlessReadableSpan(span));
  }

  @Override
  public boolean isEndRequired() {
    return true;
  }
}
