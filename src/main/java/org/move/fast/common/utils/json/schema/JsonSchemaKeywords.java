/*
 * Copyright 2013-2016 the original author or authors.
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
package org.move.fast.common.utils.json.schema;

/**
 * Json Schema 关键字
 *
 * @author Zhang Zhizheng <zhizheng118@gmail.com>
 * @since 0.0.1-SNAPSHOT
 */
public enum JsonSchemaKeywords {

    SCHEMA("$schema"),
    ID("id"),
    TITLE("title"),
    NAME("name"),
    DESCRIPTION("description"),
    TYPE("type"),
    PROPERTIES("properties"),
    ITEMS("items"),
    REQUIRED("required"),
    MINIMUM("minimum"),
    MAXIMUM("maximum"),
    EXCLUSIVEMINIMUM("exclusiveMinimum"),
    EXCLUSIVEMAXIMUM("exclusiveMaximum"),
    MINLENGTH("minLength"),
    MAXLENGTH("maxLength"),
    DEFAULT("default"),
    MINITEMS("minItems"),
    UNIQUEITEMS("uniqueItems");

    private final String keyword;

    /**
     * @param keyword
     */
    JsonSchemaKeywords(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }

}
