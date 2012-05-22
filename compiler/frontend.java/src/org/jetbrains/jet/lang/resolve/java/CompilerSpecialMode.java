/*
 * Copyright 2010-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.resolve.java;

/**
* @author Stepan Koltsov
*/
public enum CompilerSpecialMode {
    REGULAR,
    BUILTINS,
    ALT_HEADERS,
    STDLIB,
    IDEA,
    JS,
    ;

    public boolean includeAltHeaders() {
        return this == REGULAR || this == STDLIB || this == IDEA;
    }

    public boolean includeKotlinRuntime() {
        return this == REGULAR;
    }

    public boolean includeJdk() {
        return this != IDEA;
    }

    public boolean isStubs() {
        return this == BUILTINS || this == ALT_HEADERS;
    }
}