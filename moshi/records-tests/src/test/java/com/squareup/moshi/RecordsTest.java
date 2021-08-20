/*
 * Copyright (C) 2021 Square, Inc.
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
package com.squareup.moshi;

import java.io.IOException;
import java.lang.annotation.Retention;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class RecordsTest {

  private final Moshi moshi = new Moshi.Builder().build();

  @Test public void genericRecord() throws IOException {
    var adapter =
      moshi.<GenericRecord<String>>adapter(Types.newParameterizedType(GenericRecord.class,
        String.class));
    assertThat(adapter.fromJson("{\"value\":\"Okay!\"}")).isEqualTo(new GenericRecord<>("Okay!"));
  }

  @Test public void genericBoundedRecord() throws IOException {
    var adapter = moshi.<GenericBoundedRecord<Integer>>adapter(Types.newParameterizedType(
      GenericBoundedRecord.class,
      Integer.class));
    assertThat(adapter.fromJson("{\"value\":4}")).isEqualTo(new GenericBoundedRecord<>(4));
  }

  @Test public void qualifiedValues() throws IOException {
    var adapter = moshi
      .newBuilder()
      .add(new ColorAdapter())
      .build()
      .adapter(QualifiedValues.class);
    assertThat(adapter.fromJson("{\"value\":\"#ff0000\"}")).isEqualTo(new QualifiedValues(16711680));
  }

  @Test public void jsonName() throws IOException {
    var adapter = moshi.adapter(JsonName.class);
    assertThat(adapter.fromJson("{\"actualValue\":3}")).isEqualTo(new JsonName(3));
  }
}

record GenericBoundedRecord<T extends Number>(T value) {}

record GenericRecord<T>(T value) {}

record QualifiedValues(@HexColor int value) {}

@Retention(RUNTIME)
@JsonQualifier
@interface HexColor {
}

/** Converts strings like #ff0000 to the corresponding color ints. */
class ColorAdapter {
  @ToJson String toJson(@HexColor int rgb) {
    return String.format("#%06x", rgb);
  }

  @FromJson @HexColor int fromJson(String rgb) {
    return Integer.parseInt(rgb.substring(1), 16);
  }
}

record JsonName(@Json(name = "actualValue") int value) {}
