package io.opentelemetry.contrib.schema;

import io.opentelemetry.api.common.AttributeKey;

/** An interface for rename-attributes conversions. */
public interface RenameAttributes {
  /** Progress forward through transformations. */
  <T> AttributeKey<T> forward(AttributeKey<T> latest);
  /** Progress backwards through transformations. */
  <T> AttributeKey<T> reverse(AttributeKey<T> latest);
}
