plugins {
	java
	id("com.google.protobuf") version "0.9.5"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.chepenkov"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.grpc:grpc-netty-shaded:1.73.0")
	implementation("io.grpc:grpc-protobuf:1.73.0")
	implementation("io.grpc:grpc-stub:1.73.0")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.25.3"
	}
	plugins {
		create("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:1.73.0"
		}
	}
	generateProtoTasks {
		all().forEach { task ->
			task.plugins {
				create("grpc") {}
			}
		}
	}
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
	archiveBaseName.set("storage-service")
	archiveClassifier.set("")
	archiveVersion.set("")
	manifest {
		attributes["Main-Class"] = "ru.chepenkov.storage.Main"
	}
}

sourceSets {
	main {
		resources {
			exclude("**/*.proto")
		}
		proto {
			srcDir ("storage-service/src/main/proto")
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.processResources {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}