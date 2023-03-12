package io.opentelemetry.contrib.schema;

import java.util.Optional;

/**
 * Utilities for dealing with Schema URL.
 *
 * <p>Schemas are required to follow this format:
 * <code>http[s]://server[:port]/path/version</code></p>
 */
public class SchemaUrlUtil {
  private SchemaUrlUtil() {}

  public static Optional<String> getSchemaUrlVersion(String schemaUrl) {
    int lastSlash = schemaUrl.lastIndexOf('/');
    if (lastSlash > 0 && lastSlash < schemaUrl.length()-1) {
      return Optional.of(schemaUrl.substring(lastSlash+1));
    }
    return Optional.empty();
  }

  public static Optional<String> getSchemaUrlFamily(String schemaUrl) {
    int lastSlash = schemaUrl.lastIndexOf('/');
    if (lastSlash > 0 && lastSlash < schemaUrl.length()-1) {
      return Optional.of(schemaUrl.substring(0, lastSlash));
    }
    return Optional.empty();
  }

  public static boolean areCompatible(String schemaUrl, String schemaUrl2) {
    Optional<String> family1 = getSchemaUrlFamily(schemaUrl);
    Optional<String> family2 = getSchemaUrlFamily(schemaUrl2);
    return family1.isPresent() && family2.isPresent() && family1.equals(family2);
  }


}
