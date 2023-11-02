package io.opentelmetry.contrib.eventlogmixer;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import java.util.Collections;
import java.util.List;

/** SpanData that simply hides access to SpanEvents. */
class EventlessSpanData implements SpanData {
  private final SpanData delegate;

  public EventlessSpanData(SpanData delegate) {this.delegate = delegate;}


  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public SpanKind getKind() {
    return delegate.getKind();
  }

  @Override
  public SpanContext getSpanContext() {
    return delegate.getSpanContext();
  }

  @Override
  public SpanContext getParentSpanContext() {
    return delegate.getParentSpanContext();
  }

  @Override
  public StatusData getStatus() {
    return delegate.getStatus();
  }

  @Override
  public long getStartEpochNanos() {
    return delegate.getStartEpochNanos();
  }

  @Override
  public Attributes getAttributes() {
    return delegate.getAttributes();
  }

  @Override
  public List<EventData> getEvents() {
    return Collections.emptyList();
  }

  @Override
  public List<LinkData> getLinks() {
    return delegate.getLinks();
  }

  @Override
  public long getEndEpochNanos() {
    return delegate.getEndEpochNanos();
  }

  @Override
  public boolean hasEnded() {
    return delegate.hasEnded();
  }

  @Override
  public int getTotalRecordedEvents() {
    return delegate.getTotalRecordedEvents();
  }

  @Override
  public int getTotalRecordedLinks() {
    return delegate.getTotalRecordedLinks();
  }

  @Override
  public int getTotalAttributeCount() {
    return delegate.getTotalAttributeCount();
  }

  @Override
  @SuppressWarnings("deprecation") // Needed to implement interface.
  public io.opentelemetry.sdk.common.InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
    return delegate.getInstrumentationLibraryInfo();
  }

  @Override
  public Resource getResource() {
    return delegate.getResource();
  }
}
