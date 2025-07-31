plugins {
	java
	id("com.google.protobuf") version "0.9.5"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	//id("org.liquibase.gradle") version "3.0.2"
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
	implementation("io.minio:minio:8.5.17")
	compileOnly("org.projectlombok:lombok:1.18.38")
	annotationProcessor("org.projectlombok:lombok:1.18.38")
	runtimeOnly("org.postgresql:postgresql:42.7.3")
	//implementation("org.liquibase:liquibase-core")
	//implementation("org.liquibase:liquibase-core")
	implementation("org.liquibase:liquibase-core:4.25.1")
	testCompileOnly("org.projectlombok:lombok:1.18.38")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
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