package io.opentelemetry.contrib.schema;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestSchemaUrlUtil {

  @Test
  void canExtractVersion() {
    assertThat(SchemaUrlUtil.getSchemaUrlVersion("https://opentelemetry.io/schemas/1.20.0"))
        .contains("1.20.0");
  }

  @Test
  void canDetermineCompatible() {
    assertThat(SchemaUrlUtil.areCompatible("https://opentelemetry.io/schemas/1.20.0",
        "https://opentelemetry.io/schemas/1.19.0")).isTrue();
    assertThat(SchemaUrlUtil.areCompatible("https://opentelemetry.io/schemas/1.20.0",
        "https://telemetry.jsuereth.com/schemas/1.0")).isFalse();
  }
}
