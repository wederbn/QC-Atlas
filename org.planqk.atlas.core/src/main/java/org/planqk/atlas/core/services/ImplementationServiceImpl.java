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

package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ImplementationServiceImpl implements ImplementationService {

    private final ImplementationRepository implementationRepository;
    private final SoftwarePlatformRepository softwarePlatformRepository;

    private final PublicationRepository publicationRepository;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public Implementation create(@NonNull Implementation implementation, @NonNull UUID implementedAlgorithmId) {
        Algorithm implementedAlgorithm = ServiceUtils.findById(implementedAlgorithmId, Algorithm.class, algorithmRepository);
        implementation.setImplementedAlgorithm(implementedAlgorithm);
        Implementation savedImplementation = implementationRepository.save(implementation);
        implementedAlgorithm.getImplementations().add(savedImplementation);
        return savedImplementation;
    }

    @Override
    public Page<Implementation> findAll(@NonNull Pageable pageable) {
        return this.implementationRepository.findAll(pageable);
    }

    @Override
    public Implementation findById(@NonNull UUID implementationId) {
        return ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
    }

    @Override
    @Transactional
    public Implementation update(@NonNull Implementation implementation) {
        Implementation persistedImplementation = findById(implementation.getId());

        persistedImplementation.setName(implementation.getName());
        persistedImplementation.setDescription(implementation.getDescription());
        persistedImplementation.setContributors(implementation.getContributors());
        persistedImplementation.setAssumptions(implementation.getAssumptions());
        persistedImplementation.setInputFormat(implementation.getInputFormat());
        persistedImplementation.setParameter(implementation.getParameter());
        persistedImplementation.setOutputFormat(implementation.getOutputFormat());
        persistedImplementation.setLink(implementation.getLink());
        persistedImplementation.setDependencies(implementation.getDependencies());

        return implementationRepository.save(persistedImplementation);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID implementationId) {
        Implementation implementation = findById(implementationId);

        removeReferences(implementation);

        implementationRepository.deleteById(implementationId);
    }

    private void removeReferences(@NonNull Implementation implementation) {
        implementation.setImplementedAlgorithm(null);

        implementation.getPublications().forEach(publication ->
                publication.removeImplementation(implementation));

        implementation.getSoftwarePlatforms().forEach(softwarePlatform ->
                softwarePlatform.removeImplementation(implementation));
    }

    @Override
    public void checkIfImplementationIsOfAlgorithm(@NonNull UUID implementationId, @NonNull UUID algorithmId) {
        Implementation implementation = findById(implementationId);

        if (!implementation.getImplementedAlgorithm().getId().equals(algorithmId)) {
            throw new NoSuchElementException("Implementation with ID \"" + implementationId
                    + "\" of Algorithm with ID \"" + algorithmId +  "\" does not exist");
        }
    }

    @Override
    public Page<Implementation> findByImplementedAlgorithm(@NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);

        return implementationRepository.findByImplementedAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Page<SoftwarePlatform> findLinkedSoftwarePlatforms(@NonNull UUID implementationId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(implementationId, Implementation.class, implementationRepository);

        return softwarePlatformRepository.findSoftwarePlatformsByImplementationId(implementationId, pageable);
    }

    @Override
    public Page<Publication> findLinkedPublications(@NonNull UUID implementationId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(implementationId, Implementation.class, implementationRepository);

        return publicationRepository.findPublicationsByImplementationId(implementationId, pageable);
    }
}
