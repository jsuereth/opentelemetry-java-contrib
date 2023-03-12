package io.opentelemetry.contrib.schema;

import io.opentelemetry.contrib.schema.internal.SpanDataWrapper;
import io.opentelemetry.sdk.trace.data.SpanData;
import java.util.List;

public class SpanChanges {
  private final List<RenameAttributes> changes;

  public SpanChanges(List<RenameAttributes> changes) {
    this.changes = changes;
  }

  public SpanData apply(SpanData span, String schemaUrl) {
    return new SpanDataWrapper(span, new OrderedRenameAttributes(changes), schemaUrl);
  }
}
