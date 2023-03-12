package io.opentelemetry.contrib.schema;

import java.util.Map;

public class RenameMetrics {
  final Map<String, String> renames;


  public RenameMetrics(Map<String, String> renames) {
    this.renames = renames;
  }
}
