package io.opentelemetry.contrib.schema;

import io.opentelemetry.contrib.schema.internal.AttributesWrapper;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.resources.ResourceBuilder;
import java.util.List;

public class ResourceChanges {
  private final List<RenameAttributes> changes;

  public ResourceChanges(List<RenameAttributes> changes) {this.changes = changes;}

  public ResourceBuilder apply(Resource resource) {
    ResourceBuilder result = Resource.builder();
    result.putAll(new AttributesWrapper(resource.getAttributes(), new OrderedRenameAttributes(changes)));
    return result;
  }
}
