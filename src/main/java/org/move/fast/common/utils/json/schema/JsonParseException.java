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
 * Json 解析异常
 *
 * @author Zhang Zhizheng <zhizheng118@gmail.com>
 * @since 0.0.3
 */
public class JsonParseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public JsonParseException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public JsonParseException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public JsonParseException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JsonParseException(Throwable cause) {
        super(cause);
    }


}
