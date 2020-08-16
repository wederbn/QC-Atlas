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

package org.planqk.atlas.web.controller;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "tag")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.TAGS)
@AllArgsConstructor
@Slf4j
public class TagController {

    private TagService tagService;
    private TagAssembler tagAssembler;
    private AlgorithmAssembler algorithmAssembler;
    private ImplementationAssembler implementationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping(value = "/")
    @ListParametersDoc()
    public HttpEntity<PagedModel<EntityModel<TagDto>>> getTags(@Parameter(hidden = true) ListParameters listParameters) {
        return new ResponseEntity<>(tagAssembler.toModel(tagService.findAll(listParameters.getPageable(), listParameters.getSearch())), HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping(value = "/")
    public HttpEntity<EntityModel<TagDto>> createTag(@Valid @RequestBody TagDto tag) {
        Tag savedTag = tagService.save(ModelMapperUtils.convert(tag, Tag.class));
        return new ResponseEntity<>(tagAssembler.toModel(savedTag), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping(value = "/{tagId}")
    public HttpEntity<EntityModel<TagDto>> getTag(@PathVariable String name) {
        Tag tag = tagService.findByName(name);
        return new ResponseEntity<>(tagAssembler.toModel(tag), HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping(value = "/{tagId}/" + Constants.ALGORITHMS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithmsOfTag(@PathVariable String value) {
        Tag tag = this.tagService.findByName(value);
        CollectionModel<EntityModel<AlgorithmDto>> algorithms = algorithmAssembler.toModel(tag.getImplementations());
        algorithmAssembler.addLinks(algorithms.getContent());
        tagAssembler.addAlgorithmLink(algorithms, tag.getValue());
        return new ResponseEntity<>(algorithms, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping(value = "/{tagId}/" + Constants.IMPLEMENTATIONS)
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementationsOfTag(
            @PathVariable String value) {
        Tag tag = this.tagService.findByName(value);
        CollectionModel<EntityModel<ImplementationDto>> implementations = implementationAssembler.toModel(tag.getImplementations());
        implementationAssembler.addLinks(implementations.getContent());
        tagAssembler.addImplementationLink(implementations, tag.getValue());
        return new ResponseEntity<>(implementations, HttpStatus.OK);
    }
}
