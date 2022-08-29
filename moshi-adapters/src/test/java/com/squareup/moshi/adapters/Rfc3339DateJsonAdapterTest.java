/*
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.moshi.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.squareup.moshi.JsonAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public final class Rfc3339DateJsonAdapterTest {
  private final JsonAdapter<Date> adapter = new Rfc3339DateJsonAdapter().lenient();

  @Test
  public void fromJsonWithTwoDigitMillis() throws Exception {
    assertEquals(
        adapter.fromJson("\"1985-04-12T23:20:50.52Z\""), newDate(1985, 4, 12, 23, 20, 50, 520, 0));
  }

  @Test
  public void fromJson() throws Exception {
    assertEquals(
        adapter.fromJson("\"1970-01-01T00:00:00.000Z\""), newDate(1970, 1, 1, 0, 0, 0, 0, 0));
    assertEquals(
        adapter.fromJson("\"1985-04-12T23:20:50.520Z\""), newDate(1985, 4, 12, 23, 20, 50, 520, 0));
    assertEquals(
        adapter.fromJson("\"1996-12-19T16:39:57-08:00\""),
        newDate(1996, 12, 19, 16, 39, 57, 0, -8 * 60));
    assertEquals(
        adapter.fromJson("\"1990-12-31T23:59:60Z\""), newDate(1990, 12, 31, 23, 59, 59, 0, 0));
    assertEquals(
        adapter.fromJson("\"1990-12-31T15:59:60-08:00\""),
        newDate(1990, 12, 31, 15, 59, 59, 0, -8 * 60));
    assertEquals(
        adapter.fromJson("\"1937-01-01T12:00:27.870+00:20\""),
        newDate(1937, 1, 1, 12, 0, 27, 870, 20));
  }

  @Test
  public void toJson() throws Exception {
    assertEquals(
        adapter.toJson(newDate(1970, 1, 1, 0, 0, 0, 0, 0)), "\"1970-01-01T00:00:00.000Z\"");
    assertEquals(
        adapter.toJson(newDate(1985, 4, 12, 23, 20, 50, 520, 0)), "\"1985-04-12T23:20:50.520Z\"");
    assertEquals(
        adapter.toJson(newDate(1996, 12, 19, 16, 39, 57, 0, -8 * 60)),
        "\"1996-12-20T00:39:57.000Z\"");
    assertEquals(
        adapter.toJson(newDate(1990, 12, 31, 23, 59, 59, 0, 0)), "\"1990-12-31T23:59:59.000Z\"");
    assertEquals(
        adapter.toJson(newDate(1990, 12, 31, 15, 59, 59, 0, -8 * 60)),
        "\"1990-12-31T23:59:59.000Z\"");
    assertEquals(
        adapter.toJson(newDate(1937, 1, 1, 12, 0, 27, 870, 20)), "\"1937-01-01T11:40:27.870Z\"");
  }

  @Test
  public void nullSafety() throws Exception {
    assertEquals(adapter.toJson(null), "null");
    assertNull(adapter.fromJson("null"));
  }

  private Date newDate(
      int year, int month, int day, int hour, int minute, int second, int millis, int offset) {
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    calendar.set(year, month - 1, day, hour, minute, second);
    calendar.set(Calendar.MILLISECOND, millis);
    return new Date(calendar.getTimeInMillis() - TimeUnit.MINUTES.toMillis(offset));
  }
}
