/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link AlgorithmRelation}s in the database.
 */
public interface AlgorithmRelationService {

    /**
     * Creates a new database entry for a given {@link AlgorithmRelation} and save it to the database.
     * <p>
     * The ID of the {@link AlgorithmRelation} parameter should be null, since the ID will be generated by the database
     * when creating the entry. The validation for this is done by the Controller layer, which will reject {@link
     * AlgorithmRelation}s with a given ID in its create path.
     * <p>
     * The {@link org.planqk.atlas.core.model.AlgorithmRelationType} has to be set and can not be null. However, only
     * the ID of the type has to be set since the correct type object will be queried from the database. This way we can
     * check if the given type exists in the database without another checking step. If the {@link
     * org.planqk.atlas.core.model.AlgorithmRelationType} with given ID doesn't exist a {@link
     * java.util.NoSuchElementException} is thrown.
     * <p>
     * The same approach is taken for the source and target {@link org.planqk.atlas.core.model.Algorithm}s of the {@link
     * AlgorithmRelation}. All other properties of the source and target algorithm in the {@link AlgorithmRelation}
     * parameter will be ignored, except the IDs which will also be used to query both {@link
     * org.planqk.atlas.core.model.Algorithm}s from the database. This way we will also check if the given {@link
     * org.planqk.atlas.core.model.Algorithm}s exist in the database without another step checking for this. If either
     * of the algorithms doesn't exist a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmRelation The {@link AlgorithmRelation} object describing the relation between a source and a
     *                          target algorithm that should be saved to the database
     * @return The {@link AlgorithmRelation} object that represents the saved status of the database
     */
    @Transactional
    AlgorithmRelation create(AlgorithmRelation algorithmRelation);

    /**
     * Find a database entry of a {@link AlgorithmRelation} that is already saved in the database. This search is based
     * on the ID the database has given the {@link AlgorithmRelation} object when it was created and first saved to the
     * database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param algorithmRelationId The ID of the {@link AlgorithmRelation} we want to find
     * @return The {@link AlgorithmRelation} with the given ID
     */
    AlgorithmRelation findById(UUID algorithmRelationId);

    /**
     * Update an existing {@link AlgorithmRelation} database entry by saving the updated {@link AlgorithmRelation}
     * object to the the database.
     * <p>
     * The ID of the {@link AlgorithmRelation} parameter has to be set to the ID of the database entry we want to
     * update. The validation for this ID to be set is done by the Controller layer, which will reject {@link
     * AlgorithmRelation}s without a given ID in its update path. This ID will be used to query the existing {@link
     * AlgorithmRelation} entry we want to update. If no {@link AlgorithmRelation} entry with the given ID is found this
     * method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * The {@link org.planqk.atlas.core.model.AlgorithmRelationType} has to be set and can not be null. However, only
     * the ID of the type has to be set since the correct type object will be queried from the database in order to
     * reduce the error margin for user input. This way we can check if the given type exists in the database without
     * another checking step. If the {@link org.planqk.atlas.core.model.AlgorithmRelationType} with given ID doesn't
     * exist a {@link java.util.NoSuchElementException} is thrown. In the update process the type will not be updated
     * itself.
     *
     * @param algorithmRelation The {@link AlgorithmRelation} we want to update with its updated properties
     * @return the updated {@link AlgorithmRelation} object that represents the updated status of the database
     */
    @Transactional
    AlgorithmRelation update(AlgorithmRelation algorithmRelation);

    /**
     * Delete an existing {@link AlgorithmRelation} entry from the database. This deletion is based on the ID the
     * database has given the {@link AlgorithmRelation} when it was created and first saved to the database.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param algorithmRelationId The ID of the {@link AlgorithmRelation} we want to delete
     */
    @Transactional
    void delete(UUID algorithmRelationId);

    /**
     * Checks if a {@link org.planqk.atlas.core.model.Algorithm} is either target or source algorithm of a {@link
     * AlgorithmRelation}. In order to uniquely identify the {@link org.planqk.atlas.core.model.Algorithm} and the
     * {@link AlgorithmRelation} their IDs, which are given as parameters to this method, will be used.
     * <p>
     * If there is no {@link AlgorithmRelation} found with the ID given in the {@param algorithmRelationId} parameter or
     * if there is a {@link AlgorithmRelation}  found but neither the target nor source algorithm match the ID given in
     * the {@param algorithmId} parameter a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId         The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to check
     * @param algorithmRelationId The ID of the {@link AlgorithmRelation} we want to check
     */
    void checkIfAlgorithmIsInAlgorithmRelation(UUID algorithmId, UUID algorithmRelationId);
}
