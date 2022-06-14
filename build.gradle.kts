repositories {
	maven { setUrl("https://repo.spring.io/release") }
	maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
	maven { setUrl("https://mirrors.tencent.com/nexus/repository/maven-public/") }
	maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
	maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
}

plugins {
	val kotlinVersion = "1.6.21"
	kotlin("jvm") version kotlinVersion
	kotlin("kapt") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion
}

dependencies {
	api(platform(kotlin("bom")))
	api(platform("io.arrow-kt:arrow-stack:1.1.2"))
	api(platform("org.springframework:spring-framework-bom:5.3.20"))

	setOf("stdlib-jdk8", "reflect").map(::kotlin)
		.plus(setOf("io.arrow-kt:arrow-core"))
		.plus(setOf("org.springframework:spring-webmvc", "org.springframework:spring-core"))
		.plus(setOf("com.querydsl:querydsl-jpa:5.0.0"))
		.plus(setOf("com.fasterxml.jackson.core:jackson-databind:2.13.3"))
		.plus(setOf("com.fasterxml.jackson.module:jackson-module-kotlin"))
		.plus(setOf("com.baomidou:mybatis-plus-core:3.3.2"))
		.forEach(::api)

	testImplementation(platform("org.springframework.boot:spring-boot-dependencies:2.5.6"))

	testImplementation("com.querydsl:querydsl-apt:5.0.0")
	testImplementation("javax.annotation:javax.annotation-api:1.3.2")

	testImplementation("org.springframework.boot:spring-boot-starter-web:2.7.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.0")
	testImplementation("com.h2database:h2")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.3.0")
	testImplementation("com.baomidou:mybatis-plus-boot-starter:3.5.2")

	setOf("test-junit5").map(::kotlin)
		.forEach(::testImplementation)

	listOf("com.querydsl:querydsl-apt:5.0.0:jpa").forEach { kaptTest(it) }
}

tasks {
	test {
		useJUnitPlatform()
	}

	val javaVersion = JavaVersion.VERSION_17.toString()
	compileKotlin {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = javaVersion
		}
	}
	compileTestKotlin {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = javaVersion
		}
	}
	compileJava {
		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion
	}
	compileTestJava {
		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion
	}
}