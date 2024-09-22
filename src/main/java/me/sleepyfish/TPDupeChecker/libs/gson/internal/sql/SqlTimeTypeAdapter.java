/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.sleepyfish.TPDupeChecker.libs.gson.internal.sql;

import me.sleepyfish.TPDupeChecker.libs.gson.Gson;
import me.sleepyfish.TPDupeChecker.libs.gson.JsonSyntaxException;
import me.sleepyfish.TPDupeChecker.libs.gson.TypeAdapter;
import me.sleepyfish.TPDupeChecker.libs.gson.TypeAdapterFactory;
import me.sleepyfish.TPDupeChecker.libs.gson.reflect.TypeToken;
import me.sleepyfish.TPDupeChecker.libs.gson.stream.JsonReader;
import me.sleepyfish.TPDupeChecker.libs.gson.stream.JsonToken;
import me.sleepyfish.TPDupeChecker.libs.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adapter for java.sql.Time. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      return typeToken.getRawType() == Time.class ? (TypeAdapter<T>) new SqlTimeTypeAdapter() : null;
    }
  };

  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

  private SqlTimeTypeAdapter() {
  }

  @Override public synchronized Time read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    try {
      Date date = format.parse(in.nextString());
      return new Time(date.getTime());
    } catch (ParseException e) {
      throw new JsonSyntaxException(e);
    }
  }

  @Override public synchronized void write(JsonWriter out, Time value) throws IOException {
    out.value(value == null ? null : format.format(value));
  }
}
