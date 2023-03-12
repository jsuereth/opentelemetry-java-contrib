package io.opentelemetry.contrib.schema.internal;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.contrib.schema.RenameAttributes;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** Wraps the attributes class and transforms keys. */
public class AttributesWrapper implements Attributes {
  final Attributes original;
  final RenameAttributes transformation;

  public AttributesWrapper(Attributes original,
      RenameAttributes transformation) {
    this.original = original;
    this.transformation = transformation;
  }

  @Nullable
  @Override
  public <T> T get(AttributeKey<T> key) {
    return original.get(transformation.reverse(key));
  }

  @Override
  public void forEach(BiConsumer<? super AttributeKey<?>, ? super Object> consumer) {
    original.forEach((k,v) -> consumer.accept(transformation.forward(k), v));
  }

  @Override
  public int size() {
    return original.size();
  }

  @Override
  public boolean isEmpty() {
    return original.isEmpty();
  }

  @Override
  public Map<AttributeKey<?>, Object> asMap() {
    Map<AttributeKey<?>, Object> result = new HashMap<>();
    forEach(result::put);
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public AttributesBuilder toBuilder() {
    AttributesBuilder builder = Attributes.builder();
    forEach((k,v) -> builder.put((AttributeKey<Object>)k,v));
    return builder;
  }
}
