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

import tools.jackson.databind.json.JsonMapper;

public final class Jackson2JsonMapper {

    private Jackson2JsonMapper() {
    }

    public static JsonMapper.Builder builder() {
        return JsonMapper.builderWithJackson2Defaults()
            .accessorNaming(new Jackson2AccessorNamingStrategy.Provider());
    }
}
