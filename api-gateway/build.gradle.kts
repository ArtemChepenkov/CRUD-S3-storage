plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.5"
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.liquibase:liquibase-core")
	implementation("io.grpc:grpc-netty-shaded:1.73.0")
	implementation("io.grpc:grpc-protobuf:1.73.0")
	implementation("io.grpc:grpc-stub:1.73.0")
	//implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
	//implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	compileOnly("org.projectlombok:lombok")
	//compileOnly("javax.annotation:javax.annotation-api:1.3.2")
	//compileOnly("org.apache.tomcat:annotations-api:6.0.53")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
				create("grpc") {
					//option("disable_version_check=true")
					//option("annotate_code=false")
				}
			}
		}
	}
}

sourceSets {
	main {
		resources {
			exclude("**/*.proto")
		}
		proto {
			srcDir ("api-gateway/src/main/proto")
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.processResources {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}