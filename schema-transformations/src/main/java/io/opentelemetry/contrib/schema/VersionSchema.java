package io.opentelemetry.contrib.schema;

import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.SpanData;

public class VersionSchema {
  final String schemaUrl;
  final String version;
  final SpanChanges spanChanges;
  final ResourceChanges resourceChanges;

  public VersionSchema(String schemaUrl, String version, SpanChanges spanChanges,
      ResourceChanges resourceChanges) {
    this.schemaUrl = schemaUrl;
    this.version = version;
    this.spanChanges = spanChanges;
    this.resourceChanges = resourceChanges;
  }


  // TODO - we need an external mechanism to determine which of these version bumps are in scope to run.
  public Resource advance(Resource resource) {
    return
    // TODO - we need to extract the version and check if the resource is our version.
    resourceChanges.apply(resource)
        .setSchemaUrl(schemaUrl)
        .build();
  }

  public SpanData advance(SpanData span) {
    return spanChanges.apply(span, schemaUrl);
  }
}
