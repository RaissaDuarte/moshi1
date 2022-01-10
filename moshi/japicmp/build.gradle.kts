import me.champeau.gradle.japicmp.JapicmpTask

plugins {
  `java-library`
  id("me.champeau.gradle.japicmp")
}

val baseline = configurations.create("baseline")
val latest = configurations.create("latest")

dependencies {
  baseline("com.squareup.moshi:moshi:1.13.0") {
    isTransitive = false
    isForce = true
  }
  latest(project(":moshi"))
}

val japicmp = tasks.register<JapicmpTask>("japicmp") {
  dependsOn("jar")
  oldClasspath = baseline
  newClasspath = latest
  isOnlyBinaryIncompatibleModified = true
  isFailOnModification = true
  txtOutputFile = file("$buildDir/reports/japi.txt")
  isIgnoreMissingClasses = true
  isIncludeSynthetic = true
  classExcludes = listOf(
    "com.squareup.moshi.internal.NonNullJsonAdapter", // Internal.
    "com.squareup.moshi.internal.NullSafeJsonAdapter", // Internal.
    "com.squareup.moshi.internal.Util" // Internal.
  )
  methodExcludes = listOf(
    "com.squareup.moshi.JsonAdapter#indent(java.lang.String)" // Was unintentionally open before
  )
  fieldExcludes = listOf(
    "com.squareup.moshi.CollectionJsonAdapter#FACTORY" // False-positive, class is not public anyway
  )
}

tasks.named("check").configure {
  dependsOn(japicmp)
}
