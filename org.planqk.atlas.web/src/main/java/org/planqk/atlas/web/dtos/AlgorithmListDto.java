/********************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import lombok.Getter;
import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for multiple Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
public class AlgorithmListDto extends RepresentationModel<AlgorithmListDto> {

    @Getter
    private final List<AlgorithmDto> algorithmDtos = Lists.newArrayList();

    public void add(final List<AlgorithmDto> algorithms) {
        this.algorithmDtos.addAll(algorithms);
    }

    public void add(final AlgorithmDto algorithm) {
        this.algorithmDtos.add(algorithm);
    }

    @JsonIgnore
    private Map<String, Object> otherData = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOtherJsonData() {
        return otherData;
    }

    @JsonAnySetter
    public void setOtherJsonData(String key, Object value) {
        otherData.put(key, value);
    }
}
