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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.DtoEntityConverter;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private DtoEntityConverter modelConverter;
    @Autowired
	private PagedResourcesAssembler<AlgorithmDto> paginationAssembler;
    @Autowired
    private ProblemTypeAssembler problemTypeAssembler;
    @Autowired
    private TagAssembler tagAssembler;
    @Autowired
    private AlgorithmAssembler algorithmAssembler;
    @Autowired
    private AlgorithmRelationAssembler algorithmRelationAssembler;

    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<AlgorithmDto>>> getAlgorithms(@RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all algorithms received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get Page of DTOs
        Page<AlgorithmDto> pageDto = ModelMapperUtils.convertPage(algorithmService.findAll(p), AlgorithmDto.class);
        // Generate PagedModel
        PagedModel<EntityModel<AlgorithmDto>> outputDto = paginationAssembler.toModel(pageDto);
        algorithmAssembler.addLinks(outputDto.getContent());
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<EntityModel<AlgorithmDto>> createAlgorithm(@Validated @RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");
        // store and return algorithm
        Algorithm algorithm = algorithmService.save(ModelMapperUtils.convert(algo, Algorithm.class));
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public HttpEntity<EntityModel<AlgorithmDto>> updateAlgorithm(@PathVariable UUID id, @Validated @RequestBody AlgorithmDto algo) throws NotFoundException {
        LOG.debug("Put to update algorithm with id '" + id + "' received");
        Algorithm updatedAlgorithm = algorithmService.update(id, ModelMapperUtils.convert(algo, Algorithm.class));
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<AlgorithmDto> deleteAlgorithm(@PathVariable UUID id) throws NotFoundException {
        LOG.debug("Delete to remove algorithm with id '" + id + "' received");
        algorithmService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID id) {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} from the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(algorithmOptional.get(), AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.TAGS)
    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID id) {
        Optional<Algorithm> algorithmOptional = algorithmService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Get Tags of Algorithm
        Set<Tag> tags = algorithmOptional.get().getTags();
        // Translate Entity to DTO
        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
        // Fill EntityModel Links
        tagAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addTagLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<?> getProblemTypes(@PathVariable UUID id) {
        Optional<Algorithm> algorithmOptional = algorithmService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithmOptional.get().getProblemTypes();
        // Translate Entity to DTO
        Set<ProblemTypeDto> dtoTypes = ModelMapperUtils.convertSet(problemTypes, ProblemTypeDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addProblemTypeLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @GetMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelations(@PathVariable UUID sourceAlgorithm_id) {
        Optional<Algorithm> optAlgorithm = algorithmService.findById(sourceAlgorithm_id);
        if (!optAlgorithm.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", sourceAlgorithm_id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Get AlgorithmRelationDTOs of Algorithm
        Set<AlgorithmRelationDto> algorithmRelations = ModelMapperUtils.convertSet(optAlgorithm.get().getAlgorithmRelations(), AlgorithmRelationDto.class);
        // Generate CollectionModel
        CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection = HateoasUtils.generateCollectionModel(algorithmRelations);
        // Fill EntityModel Links
        algorithmRelationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addAlgorithmRelationLink(resultCollection, sourceAlgorithm_id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @PutMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<AlgorithmRelationDto> updateAlgorithmRelation(@PathVariable UUID sourceAlgorithm_id,
    		@Validated @RequestBody AlgorithmRelationDto relation) throws NotFoundException {
        LOG.debug("Post to add algorithm relation received.");
        
        
        AlgorithmRelation algorithmRelation = algorithmService.addUpdateAlgorithmRelation(sourceAlgorithm_id, modelConverter.convert(relation));
        return new ResponseEntity<>(modelConverter.convert(algorithmRelation), HttpStatus.OK);
    }

    @DeleteMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS + "/{relation_id}")
    public HttpEntity<AlgorithmDto> deleteAlgorithmRelation(@PathVariable UUID sourceAlgorithm_id, @PathVariable UUID relation_id)
    		throws NotFoundException {
        LOG.debug("Delete received to remove algorithm relation with id {}.", relation_id);
        if (!algorithmService.deleteAlgorithmRelation(sourceAlgorithm_id, relation_id)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
