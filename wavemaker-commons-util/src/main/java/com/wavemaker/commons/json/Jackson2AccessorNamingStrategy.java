/*******************************************************************************
 * Copyright (C) 2026 WaveMaker, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wavemaker.commons.json;

import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AccessorNamingStrategy;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;

public class Jackson2AccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    public Jackson2AccessorNamingStrategy(MapperConfig<?> config, AnnotatedClass forClass) {
        super(config, forClass, "set", "get", "is", null);
    }

    protected Jackson2AccessorNamingStrategy(MapperConfig<?> config, AnnotatedClass forClass, String mutatorPrefix,
                                             String getterPrefix, String isGetterPrefix, BaseNameValidator baseNameValidator) {
        super(config, forClass, mutatorPrefix, getterPrefix, isGetterPrefix, baseNameValidator);
    }

    @Override
    protected String stdManglePropertyName(String basename, int offset) {
        int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c = basename.charAt(offset);
        char d = Character.toLowerCase(c);
        if (c == d) {
            return basename.substring(offset);
        }
        StringBuilder sb = new StringBuilder(end - offset);
        sb.append(d);
        for (int i = offset + 1; i < end; ++i) {
            c = basename.charAt(i);
            d = Character.toLowerCase(c);
            if (c == d) {
                sb.append(basename, i, end);
                break;
            }
            sb.append(d);
        }
        return sb.toString();
    }

    public static class Provider extends DefaultAccessorNamingStrategy.Provider {

        @Override
        public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass forClass) {
            return new Jackson2AccessorNamingStrategy(config, forClass);
        }

        @Override
        public AccessorNamingStrategy forBuilder(MapperConfig<?> config, AnnotatedClass builderClass, BeanDescription valueTypeDesc) {
            AnnotationIntrospector introspector = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
            JsonPOJOBuilder.Value builderConfig = introspector == null ? null : introspector.findPOJOBuilderConfig(config, builderClass);
            String mutatorPrefix = builderConfig == null ? _withPrefix : builderConfig.withPrefix;
            return new Jackson2AccessorNamingStrategy(config, builderClass, mutatorPrefix, _getterPrefix, _isGetterPrefix, _baseNameValidator);
        }
    }
}
