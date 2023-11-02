plugins {
  id("otel.java-conventions")
}

description = "Exporter implementations that store signals on disk"
otelJava.moduleName.set("io.opentelemetry.contrib.eventlogmixer")

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api("io.opentelemetry:opentelemetry-sdk")
  compileOnly("com.google.auto.value:auto-value-annotations")
  annotationProcessor("com.google.auto.value:auto-value")
  testImplementation("org.mockito:mockito-inline")
  testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
}
