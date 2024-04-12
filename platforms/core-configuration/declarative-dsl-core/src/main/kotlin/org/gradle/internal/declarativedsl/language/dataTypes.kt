/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.internal.declarativedsl.language

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.declarative.dsl.schema.DataType


@Serializable
@SerialName("int")
data object IntDataType : DataType {

    override fun isConstant(): Boolean = true

    override fun getConstantType(): Class<Int> = Int::class.java

    override fun toString(): String = "Int"
}


@Serializable
@SerialName("long")
data object LongDataType : DataType {

    override fun isConstant(): Boolean = true

    override fun getConstantType(): Class<Long> = Long::class.java

    override fun toString(): String = "Long"
}


@Serializable
@SerialName("string")
data object StringDataType : DataType {

    override fun isConstant(): Boolean = true

    override fun getConstantType(): Class<String> = String::class.java

    override fun toString(): String = "String"
}


@Serializable
@SerialName("boolean")
data object BooleanDataType : DataType {

    override fun isConstant(): Boolean = true
    override fun getConstantType(): Class<Boolean> = Boolean::class.java

    override fun toString(): String = "Boolean"
}


@Serializable
@SerialName("null")
data object NullDataType : DataType { // TODO: implement nulls?

    override fun isNull(): Boolean = true
}


@Serializable
@SerialName("unit")
data object UnitDataType : DataType {

    override fun isUnit(): Boolean = true
}

// TODO: `Any` type?
// TODO: Support subtyping of some sort in the schema rather than via reflection?
