package io.opentelemetry.contrib.schema;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Collection;
import java.util.stream.Collectors;

/** A span exporter that can update outgoing spans to a specific schema version. */
public class SchemaUpdatingSpanExporter implements SpanExporter {

  private final VersionSchema versionSchema;
  private final SpanExporter exporter;

  public SchemaUpdatingSpanExporter(VersionSchema versionSchema,
      SpanExporter exporter) {
    this.versionSchema = versionSchema;
    this.exporter = exporter;
  }

  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    return exporter.export(spans.stream().map(versionSchema::advance).collect(Collectors.toList()));
  }

  @Override
  public CompletableResultCode flush() {
    return exporter.flush();
  }

  @Override
  public CompletableResultCode shutdown() {
    return exporter.shutdown();
  }
}
