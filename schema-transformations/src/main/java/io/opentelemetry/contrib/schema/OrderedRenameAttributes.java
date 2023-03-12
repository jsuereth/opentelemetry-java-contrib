package io.opentelemetry.contrib.schema;

import io.opentelemetry.api.common.AttributeKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderedRenameAttributes implements RenameAttributes {
  final List<RenameAttributes> transformations;

  public OrderedRenameAttributes(
      List<RenameAttributes> transformations) {
    this.transformations = transformations;
  }

  @Override
  public <T> AttributeKey<T> forward(AttributeKey<T> latest) {
    AttributeKey<T> result = latest;
    for (RenameAttributes transform : transformations) {
      result = transform.forward(result);
    }
    return result;
  }

  @Override
  public <T> AttributeKey<T> reverse(AttributeKey<T> latest) {
    // TODO - less lame way to reverse a list
    List<RenameAttributes> reversed = new ArrayList<>(transformations);
    Collections.reverse(reversed);
    AttributeKey<T> result = latest;
    for (RenameAttributes transform : reversed) {
      result = transform.reverse(result);
    }
    return result;
  }
}
