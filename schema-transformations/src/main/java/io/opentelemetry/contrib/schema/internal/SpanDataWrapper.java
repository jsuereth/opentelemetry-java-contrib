package io.opentelemetry.contrib.schema.internal;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.contrib.schema.RenameAttributes;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.common.InstrumentationScopeInfoBuilder;
import io.opentelemetry.sdk.internal.InstrumentationScopeUtil;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import java.util.List;

public class SpanDataWrapper implements SpanData {
  final SpanData original;
  final RenameAttributes spanTransformation;
  final String schemaUrl;

  public SpanDataWrapper(SpanData original,
      RenameAttributes spanTransformation,
      String schemaUrl) {
    this.original = original;
    this.spanTransformation = spanTransformation;
    this.schemaUrl = schemaUrl;
  }

  @Override
  public String getName() {
    return original.getName();
  }

  @Override
  public SpanKind getKind() {
    return original.getKind();
  }

  @Override
  public SpanContext getSpanContext() {
    return original.getSpanContext();
  }

  @Override
  public SpanContext getParentSpanContext() {
    return original.getParentSpanContext();
  }

  @Override
  public StatusData getStatus() {
    return original.getStatus();
  }

  @Override
  public long getStartEpochNanos() {
    return original.getStartEpochNanos();
  }

  @Override
  public Attributes getAttributes() {
    // TODO - lazy store?
    return new AttributesWrapper(original.getAttributes(), spanTransformation);
  }

  @Override
  public List<EventData> getEvents() {
    // TODO - event renames
    return original.getEvents();
  }

  @Override
  public List<LinkData> getLinks() {
    return original.getLinks();
  }

  @Override
  public long getEndEpochNanos() {
    return original.getEndEpochNanos();
  }

  @Override
  public boolean hasEnded() {
    return original.hasEnded();
  }

  @Override
  public int getTotalRecordedEvents() {
    return original.getTotalRecordedEvents();
  }

  @Override
  public int getTotalRecordedLinks() {
    return original.getTotalRecordedLinks();
  }

  @Override
  public int getTotalAttributeCount() {
    return original.getTotalAttributeCount();
  }

  @SuppressWarnings("deprecation") // Required override.
  @Override
  public io.opentelemetry.sdk.common.InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
    return InstrumentationScopeUtil.toInstrumentationLibraryInfo(getInstrumentationScopeInfo());
  }

  @Override
  public InstrumentationScopeInfo getInstrumentationScopeInfo() {
    // TODO - memoize
    InstrumentationScopeInfoBuilder result = InstrumentationScopeInfo.builder(original.getInstrumentationScopeInfo().getName())
        // TODO - transformations here?
        .setAttributes(original.getInstrumentationScopeInfo().getAttributes())
        .setSchemaUrl(schemaUrl);
    if (original.getInstrumentationScopeInfo().getVersion() != null) {
      result.setVersion(original.getInstrumentationScopeInfo().getVersion());
    }
    return result.build();
  }

  @Override
  public Resource getResource() {
    // TODO - do we need to wrap this here?
    return original.getResource();
  }
}
