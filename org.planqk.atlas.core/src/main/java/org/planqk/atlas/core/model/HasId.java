/*******************************************************************************
 * ******************************************************************************
 *  * Copyright (c) ${YEAR} University of Stuttgart
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 *******************************************************************************/

package org.planqk.atlas.core.model;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.planqk.atlas.core.model.listener.DatabaseListener;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

/**
 * Base class defining the Id property for all JPA entity classes.
 */
@MappedSuperclass
@EntityListeners(DatabaseListener.class)
public abstract class HasId extends RepresentationModel {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
