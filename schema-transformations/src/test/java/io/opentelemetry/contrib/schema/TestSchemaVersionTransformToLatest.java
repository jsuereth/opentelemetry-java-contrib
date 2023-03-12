package io.opentelemetry.contrib.schema;


import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.testing.trace.TestSpanData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/*
file_format: 1.1.0
schema_url: https://opentelemetry.io/schemas/1.20.0
versions:
  1.20.0:
    spans:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/3272
        - rename_attributes:
            attribute_map:
              net.app.protocol.name: net.protocol.name
              net.app.protocol.version: net.protocol.version
  1.19.0:
    spans:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/3209
        - rename_attributes:
            attribute_map:
              faas.execution: faas.invocation_id
        # https://github.com/open-telemetry/opentelemetry-specification/pull/3188
        - rename_attributes:
            attribute_map:
              faas.id: cloud.resource_id
        # https://github.com/open-telemetry/opentelemetry-specification/pull/3190
        - rename_attributes:
            attribute_map:
              http.user_agent: user_agent.original
    resources:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/3190
        - rename_attributes:
            attribute_map:
              browser.user_agent: user_agent.original
  1.18.0:
  1.17.0:
    spans:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/2957
        - rename_attributes:
            attribute_map:
              messaging.consumer_id: messaging.consumer.id
              messaging.protocol: net.app.protocol.name
              messaging.protocol_version: net.app.protocol.version
              messaging.destination: messaging.destination.name
              messaging.temp_destination: messaging.destination.temporary
              messaging.destination_kind: messaging.destination.kind
              messaging.message_id: messaging.message.id
              messaging.conversation_id: messaging.message.conversation_id
              messaging.message_payload_size_bytes: messaging.message.payload_size_bytes
              messaging.message_payload_compressed_size_bytes: messaging.message.payload_compressed_size_bytes
              messaging.rabbitmq.routing_key: messaging.rabbitmq.destination.routing_key
              messaging.kafka.message_key: messaging.kafka.message.key
              messaging.kafka.partition: messaging.kafka.destination.partition
              messaging.kafka.tombstone: messaging.kafka.message.tombstone
              messaging.rocketmq.message_type: messaging.rocketmq.message.type
              messaging.rocketmq.message_tag: messaging.rocketmq.message.tag
              messaging.rocketmq.message_keys: messaging.rocketmq.message.keys
              messaging.kafka.consumer_group: messaging.kafka.consumer.group
  1.16.0:
  1.15.0:
    spans:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/2743
        - rename_attributes:
            attribute_map:
              http.retry_count: http.resend_count
  1.14.0:
  1.13.0:
    spans:
      changes:
        # https://github.com/open-telemetry/opentelemetry-specification/pull/2614
        - rename_attributes:
            attribute_map:
              net.peer.ip: net.sock.peer.addr
              net.host.ip: net.sock.host.addr
  1.12.0:
  1.11.0:
  1.10.0:
  1.9.0:
  1.8.0:
    spans:
      changes:
        - rename_attributes:
            attribute_map:
              db.cassandra.keyspace: db.name
              db.hbase.namespace: db.name
  1.7.0:
  1.6.1:
  1.5.0:
  1.4.0:
 */

public class TestSchemaVersionTransformToLatest {

  private static Map<String, String> mapOf(String k, String v) {
    return Collections.singletonMap(k, v);
  }
  private static Map<String, String> mapOf(String k, String v, String k2, String v2) {
    // TODO - more efficient data structure?
    Map<String, String> result = new HashMap<>(2);
    result.put(k,v);
    result.put(k2,v2);
    return result;
  }
  private final VersionSchema schema_1_20_0 =
      new VersionSchema(
          "https://opentelemetry.io/schemas/1.20.0",
          "1.20.0",
          new SpanChanges(Collections.singletonList(
              //  # https://github.com/open-telemetry/opentelemetry-specification/pull/3272
              new SingleRenameAttributes(
                  mapOf("net.app.protocol.name", "net.protocol.name",
                      "net.app.protocol.version","net.protocol.version")
              )
          )), new ResourceChanges(Collections.emptyList()));

  private final VersionSchema schema_1_19_0 =
      new VersionSchema(
          "https://opentelemetry.io/schemas/1.19.0",
          "1.19.0",
          new SpanChanges(Arrays.asList(
              // # https://github.com/open-telemetry/opentelemetry-specification/pull/3209
              //        - rename_attributes:
              //            attribute_map:
              //              faas.execution: faas.invocation_id
              new SingleRenameAttributes(
                  mapOf("faas.executioe", "faas.invocation_id")),
              //        # https://github.com/open-telemetry/opentelemetry-specification/pull/3188
              //        - rename_attributes:
              //            attribute_map:
              //              faas.id: cloud.resource_id
              new SingleRenameAttributes(
                  mapOf("faas.id", "cloud.resource_id")),
              //        # https://github.com/open-telemetry/opentelemetry-specification/pull/3190
              //        - rename_attributes:
              //            attribute_map:
              //              http.user_agent: user_agent.original
              new SingleRenameAttributes(
                  mapOf("http.user_agent", "user_agent.original"))
          )),
          new ResourceChanges(Collections.singletonList(
              //  # https://github.com/open-telemetry/opentelemetry-specification/pull/3190
              //        - rename_attributes:
              //            attribute_map:
              //              browser.user_agent: user_agent.original
              new SingleRenameAttributes(
                  mapOf("browser.user_agent", "user_agent.original"))
          )));


  @Test
  public void testSingleSchemaTransformationOnResource() {
    Resource startingPoint =
        Resource.builder()
            .put(AttributeKey.stringKey("browser.user_agent"), "Not Chrome")
            .setSchemaUrl("https://opentelemetry.io/schemas/1.18.0")
            .build();
    Resource result = schema_1_19_0.advance(startingPoint);
    assertThat(result.getAttributes()).containsEntry(
        AttributeKey.stringKey("user_agent.original"), "Not Chrome");
    assertThat(result.getSchemaUrl())
        .isEqualTo("https://opentelemetry.io/schemas/1.19.0");

  }

  @Test
  public void testSingleSchemaTransformationOnSpan() {
    TestSpanData span = TestSpanData.builder()
        .setAttributes(Attributes.builder()
            .put(AttributeKey.stringKey("net.app.protocol.name"), "HTTP")
            .build())
        .setName("test-span")
        .setStartEpochNanos(1L)
        .setEndEpochNanos(3L)
        .setStatus(StatusData.ok())
        .setHasEnded(true)
        .setKind(SpanKind.CLIENT)
        .setResource(Resource.builder()
                .put(AttributeKey.stringKey("browser.user_agent"), "Not Chrome")
                .setSchemaUrl("https://opentelemetry.io/schemas/1.18.0")
                .build())
        .setInstrumentationScopeInfo(
            InstrumentationScopeInfo.builder("test-inst")
                .setSchemaUrl("https://opentelemetry.io/schemas/1.18.0")
                .build()
        )
        .build();

    SpanData result = schema_1_20_0.advance(span);
    assertThat(result.getAttributes()).containsEntry(
        AttributeKey.stringKey("net.protocol.name"), "HTTP");
    assertThat(result.getInstrumentationScopeInfo().getSchemaUrl())
        .isEqualTo("https://opentelemetry.io/schemas/1.20.0");
  }
}
