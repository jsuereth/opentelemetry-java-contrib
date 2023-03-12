package io.opentelemetry.contrib.schema;

import java.util.Map;

public class RenameEvents {
  final Map<String,String> renames;

  public RenameEvents(Map<String, String> renames) {this.renames = renames;}
}
