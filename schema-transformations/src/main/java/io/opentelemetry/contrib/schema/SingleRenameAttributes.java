package io.opentelemetry.contrib.schema;

import io.opentelemetry.api.common.AttributeKey;
import java.util.Map;

/** Data class representing the rename-attributes transformation. */
public class SingleRenameAttributes implements RenameAttributes{
  // TODO - use bi-map
  final Map<String, String> attributeMap;


  public SingleRenameAttributes(Map<String, String> attributeMap) {
    this.attributeMap = attributeMap;
  }

  @Override
  public <T> AttributeKey<T> forward(AttributeKey<T> latest) {
    // Need to return previous from latest.
    if (attributeMap.containsKey(latest.getKey())) {
      return renamedKey(latest, attributeMap.get(latest.getKey()));
    }
    return latest;
  }

  @Override
  public <T> AttributeKey<T> reverse(AttributeKey<T> latest) {
    // Need to return previous from latest.
    for (Map.Entry<String,String> entry: attributeMap.entrySet()) {
      if (entry.getValue().equals(latest.getKey())) {
        return renamedKey(latest, entry.getKey());
      }
    }
    return latest;
  }

  @SuppressWarnings("unchecked")
  private static <T> AttributeKey<T> renamedKey(AttributeKey<T> latest, String key) {
    switch (latest.getType()) {
      case LONG:
        return (AttributeKey<T>) AttributeKey.longKey(key);
      case LONG_ARRAY:
        return (AttributeKey<T>) AttributeKey.longArrayKey(key);
      case DOUBLE:
        return (AttributeKey<T>) AttributeKey.doubleKey(key);
      case DOUBLE_ARRAY:
        return (AttributeKey<T>) AttributeKey.doubleArrayKey(key);
      case STRING:
        return (AttributeKey<T>) AttributeKey.stringKey(key);
      case STRING_ARRAY:
        return (AttributeKey<T>) AttributeKey.stringArrayKey(key);
      case BOOLEAN:
        return (AttributeKey<T>) AttributeKey.booleanKey(key);
      case BOOLEAN_ARRAY:
        return (AttributeKey<T>) AttributeKey.booleanArrayKey(key);
    }
    // We should never reach this.
    return latest;
  }


}
