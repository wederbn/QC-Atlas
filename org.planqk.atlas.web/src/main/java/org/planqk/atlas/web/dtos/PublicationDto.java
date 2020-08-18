/*******************************************************************************
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

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.web.utils.ValidationGroups.Update;
import org.planqk.atlas.web.utils.ValidationGroups.Create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@Relation(itemRelation = "publication", collectionRelation = "publications")
public class PublicationDto {

    @NotNull(groups = {Update.class}, message = "The id must not be null to perform an update")
    @Null(groups = {Create.class}, message = "When Creating a resource the id must be null")
    private UUID id;

    @NotNull(groups = {Create.class}, message = "Title of the Publication must not be null!")
    private String title;

    private String doi;

    @Schema(description = "URL", example = "https://www.ibm.com/quantum-computing/", required = false)
    @URL(groups = {Create.class}, message = "Publication URL must be a valid URL!")
    private String url;

    @NotEmpty(groups = {Create.class}, message = "Authors of the Publication must not be empty!")
    private List<String> authors;
}
