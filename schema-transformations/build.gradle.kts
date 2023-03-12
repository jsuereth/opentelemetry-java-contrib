plugins {
  id("otel.java-conventions")
}

description = "A library for doing schema transformations in the Java SDK."
otelJava.moduleName.set("io.opentelemetry.contrib.schema-transformations")

dependencies {
  api("io.opentelemetry:opentelemetry-sdk")
  api("io.opentelemetry:opentelemetry-semconv")
  testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
}
