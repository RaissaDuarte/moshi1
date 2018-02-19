/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(RUNTIME)
@Target(CLASS)
annotation class MoshiSerializable

class MoshiSerializableFactory : JsonAdapter.Factory {

  override fun create(type: Type, annotations: MutableSet<out Annotation>,
      moshi: Moshi): JsonAdapter<*>? {

    val rawType = Types.getRawType(type)
    if (!rawType.isAnnotationPresent(MoshiSerializable::class.java)) {
      return null
    }

    val constructor = findConstructorForClass(rawType) ?: return null

    try {
      return if (constructor.parameterTypes.size == 1) {
        constructor.newInstance(moshi)
      } else {
        if (type is ParameterizedType) {
          constructor.newInstance(moshi, type.actualTypeArguments)
        } else {
          throw IllegalStateException("Unable to handle type $type")
        }
      }
    } catch (e: IllegalAccessException) {
      throw RuntimeException("Unable to invoke " + constructor, e)
    } catch (e: InstantiationException) {
      throw RuntimeException("Unable to invoke " + constructor, e)
    } catch (e: InvocationTargetException) {
      val cause = e.cause
      if (cause is RuntimeException) {
        throw cause
      }
      if (cause is Error) {
        throw cause
      }
      throw RuntimeException(
          "Could not create generated JsonAdapter instance for type " + rawType, cause)
    }

  }

  private fun findConstructorForClass(cls: Class<*>): Constructor<out JsonAdapter<*>>? {
    var adapterCtor: Constructor<out JsonAdapter<*>>?
    val clsName = cls.name.replace("$", "_")
    try {
      val bindingClass = cls.classLoader
          .loadClass(clsName + "JsonAdapter")
      adapterCtor = try {
        // Try the moshi constructor
        @Suppress("UNCHECKED_CAST")
        bindingClass.getConstructor(
            Moshi::class.java) as Constructor<out JsonAdapter<*>>
      } catch (e: NoSuchMethodException) {
        // Try the moshi + type constructor
        @Suppress("UNCHECKED_CAST")
        bindingClass.getConstructor(Moshi::class.java,
            Array<Type>::class.java) as Constructor<out JsonAdapter<*>>
      }

    } catch (e: ClassNotFoundException) {
      adapterCtor = findConstructorForClass(cls.superclass)
    } catch (e: NoSuchMethodException) {
      throw RuntimeException("Unable to find binding constructor for " + clsName, e)
    }

    return adapterCtor
  }
}
