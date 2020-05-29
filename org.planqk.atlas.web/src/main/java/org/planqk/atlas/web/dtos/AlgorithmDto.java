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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.*;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

/**
 * Data transfer object for Algorithms
 * ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@EqualsAndHashCode
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "computationModel", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = QuantumAlgorithmDto.class, name = "QUANTUM"),
    @JsonSubTypes.Type(value = ClassicAlgorithmDto.class, name = "CLASSIC") }
)
public class AlgorithmDto {

    private UUID id;

    @NotNull(message = "Algorithm-Name must not be null!")
	private String name;

	private String problem;

	private String inputFormat;

	private String outputFormat;

	@NotNull(message = "Computational-Model must not be null!")
	private ComputationModel computationModel;

	@Size(min=1, message = "Algorithm must have at least 1 ProblemType!")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Schema(accessMode = WRITE_ONLY)
	private Set<ProblemTypeDto> problemTypes;

	// we do not embedded tags into the object (via @jsonInclude) - instead, we add
	// a hateoas link to the associated tags
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	// annotate this for swagger as well, because swagger doesn't recognize the json
	// property annotation
	@Schema(accessMode = WRITE_ONLY)
	private Set<TagDto> tags;

  public static final class Converter {

		public static AlgorithmDto convert(final Algorithm object) {
			final AlgorithmDto dto = new AlgorithmDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setInputFormat(object.getInputFormat());
            dto.setOutputFormat(object.getInputFormat());
            dto.setComputationModel(object.getComputationModel());
            dto.setProblemTypes(object.getProblemTypes().stream().map(ProblemTypeDto.Converter::convert).collect(Collectors.toSet()));
            return dto;
		}

		public static Algorithm convert(final AlgorithmDto object) {
			final Algorithm algo = new Algorithm();
            algo.setName(object.getName());
            algo.setInputFormat(object.getInputFormat());
            algo.setOutputFormat(object.getInputFormat());
            algo.setComputationModel(object.getComputationModel());
            if (Objects.nonNull(object.getProblemTypes())) {
            	algo.setProblemTypes(object.getProblemTypes().stream().map(ProblemTypeDto.Converter::convert).collect(Collectors.toSet()));
            }

			return algo;
		}
	}
}
