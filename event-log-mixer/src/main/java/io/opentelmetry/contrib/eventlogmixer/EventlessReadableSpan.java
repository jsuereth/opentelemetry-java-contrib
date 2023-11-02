package io.opentelmetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.data.SpanData;
import javax.annotation.Nullable;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.data.SpanData;
import javax.annotation.Nullable;

class EventlessReadableSpan implements ReadableSpan {
  private final ReadableSpan delegate;

  public EventlessReadableSpan(ReadableSpan delegate) {this.delegate = delegate;}

  @Override
  public SpanContext getSpanContext() {
    return delegate.getSpanContext();
  }

  @Override
  public SpanContext getParentSpanContext() {
    return delegate.getParentSpanContext();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public SpanData toSpanData() {
    return new EventlessSpanData(delegate.toSpanData());
  }

  @SuppressWarnings("deprecation") // Still need to implement deprecated interface.
  public io.opentelemetry.sdk.common.InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
    return delegate.getInstrumentationLibraryInfo();
  }

  @Override
  public boolean hasEnded() {
    return delegate.hasEnded();
  }

  @Override
  public long getLatencyNanos() {
    return delegate.getLatencyNanos();
  }

  @Override
  public SpanKind getKind() {
    return delegate.getKind();
  }

  @Nullable
  @Override
  public <T> T getAttribute(AttributeKey<T> key) {
    return delegate.getAttribute(key);
  }
}
