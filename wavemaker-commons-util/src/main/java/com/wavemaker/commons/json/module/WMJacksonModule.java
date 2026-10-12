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

import tools.jackson.core.Version;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ser.ValueSerializerModifier;

/**
 * @author <a href="mailto:dilip.gundu@wavemaker.com">Dilip Kumar</a>
 * @since 3/10/18
 */
public class WMJacksonModule extends JacksonModule {

    private final boolean failOnCircularReferences;

    public WMJacksonModule(final boolean failOnCircularReferences) {
        this.failOnCircularReferences = failOnCircularReferences;
    }

    @Override
    public String getModuleName() {
        return "wm_module";
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(final SetupContext setupContext) {

        ValueSerializerModifier serializerModifier = new WMBeanSerializerModifier(this);

        setupContext.addSerializerModifier(serializerModifier);
    }

    public boolean isFailOnCircularReferences() {
        return failOnCircularReferences;
    }
}
