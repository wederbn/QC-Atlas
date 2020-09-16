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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PublicationController.class)
@ExtendWith( {MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class PublicationControllerTest {

    @MockBean
    private PublicationService publicationService;
    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private ImplementationService implementationService;
    @MockBean
    private LinkingService linkingService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;
    private final int size = 1;
    private final Pageable pageable = PageRequest.of(page, size);
    private Publication publication;

    @BeforeEach
    public void init() {
        publication = new Publication();
        publication.setId(UUID.randomUUID());
        publication.setAuthors(new ArrayList<>(Arrays.asList("author1", "author2")));
        publication.setUrl("http://www.atlas-is-cool.org");
        publication.setTitle("TestPublication");
        publication.setDoi("");

        when(publicationService.findById(publication.getId())).thenReturn(publication);
    }

    @Test
    public void getPublications_PublicationList() throws Exception {
        List<Publication> publications = new ArrayList<>();
        publications.add(publication);
        Page<Publication> pagePublication = new PageImpl<>(publications);

        when(publicationService.findAll(pageable, null)).thenReturn(pagePublication);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "publications",
                PublicationDto.class
        );
        assertThat(resultList.size()).isEqualTo(1);
    }

    @Test
    public void getPublications_emptyPublicationList() throws Exception {
        when(publicationService.findAll(pageable, null)).thenReturn(Page.empty());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(new ListParameters(pageable, null)));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                "publications",
                PublicationDto.class
        );
        assertThat(resultList.size()).isEqualTo(0);
    }

//    @Test
//    public void createPublication_returnPublication() throws Exception {
//        when(publicationService.save(publication)).thenReturn(publication);
//        MvcResult result = mockMvc
//                .perform(post("/" + Constants.API_VERSION + "/" + Constants.PUBLICATIONS + "/")
//                        .content(mapper.writeValueAsString(publicationDto))
//                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        EntityModel<PublicationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<>() {
//                });
//        assertThat(response.getContent().getTitle()).isEqualTo(publicationDto.getTitle());
//        assertThat(response.getContent().getDoi()).isEqualTo(publicationDto.getDoi());
//        assertThat(response.getContent().getUrl()).isEqualTo(publicationDto.getUrl());
//    }
}
