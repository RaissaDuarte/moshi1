/*
 * Copyright (C) 2020 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import org.gradle.jvm.tasks.Jar

buildscript {
  dependencies {
    classpath(kotlin("gradle-plugin", version = "1.3.72"))
  }
}

plugins {
  id("com.vanniktech.maven.publish") version "0.11.1" apply false
}

subprojects {
  repositories {
    mavenCentral()
  }

  // Configure publishing
  pluginManager.withPlugin("com.vanniktech.maven.publish") {
    configure<MavenPublishPluginExtension> {
      useLegacyMode = false
      nexus {
        groupId = "com.squareup"
      }
    }

    // Configure automatic-module-name, but only for published modules
    val automaticModuleName = providers.gradleProperty("AUTOMATIC_MODULE_NAME")
        .forUseAtConfigurationTime()
    if (automaticModuleName.isPresent) {
      val name = automaticModuleName.get()
      tasks.withType<Jar>().configureEach {
        manifest {
          attributes("Automatic-Module-Name" to name)
        }
      }
    }
  }
}
