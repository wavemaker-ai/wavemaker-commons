/*******************************************************************************
 * Copyright (C) 2022-2023 WaveMaker, Inc.
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
package com.wavemaker.commons.json.module;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.bean.BeanSerializerBase;

/**
 * @author <a href="mailto:dilip.gundu@wavemaker.com">Dilip Kumar</a>
 * @since 3/10/18
 */
public class WMBeanSerializerModifier extends ValueSerializerModifier {

    private final WMJacksonModule module;

    public WMBeanSerializerModifier(WMJacksonModule module) {
        this.module = module;
    }

    @Override
    public ValueSerializer<?> modifySerializer(
        final SerializationConfig config, final BeanDescription.Supplier beanDesc, final ValueSerializer<?> serializer) {
        if (serializer instanceof BeanSerializerBase) {
            return new CircularLoopHandlingSerializer<>((BeanSerializerBase) serializer,
                module.isFailOnCircularReferences());
        } else {
            return serializer;
        }
    }
}
