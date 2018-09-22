/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 *
 * Portions copyright 2011-2013 The MITRE Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.mitre.oauth2.repository.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.mitre.oauth2.model.AuthorizationCodeEntity;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.mitre.util.jpa.JpaUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jricher
 *
 */
@Repository
@Transactional(value="defaultTransactionManager")
public class JpaOAuth2ClientRepository implements OAuth2ClientRepository {

	@PersistenceContext(unitName="defaultPersistenceUnit")
	private EntityManager manager;

	public JpaOAuth2ClientRepository() {

	}

	public JpaOAuth2ClientRepository(EntityManager manager) {
		this.manager = manager;
	}

	@Override
	public ClientDetailsEntity getById(String hostUuid, String uuid) {
		ClientDetailsEntity entity = manager.find(ClientDetailsEntity.class, uuid);
		if(entity.getHostUuid().equals(hostUuid)) {
			throw new IllegalArgumentException("Entity not found: " + uuid);
		}
		return entity;
	}

	/* (non-Javadoc)
	 * @see org.mitre.oauth2.repository.OAuth2ClientRepository#getClientById(java.lang.String)
	 */
	@Override
	public ClientDetailsEntity getClientByClientId(String hostUuid, String clientId) {
		TypedQuery<ClientDetailsEntity> query = manager.createNamedQuery(ClientDetailsEntity.QUERY_BY_CLIENT_ID, ClientDetailsEntity.class);
		query.setParameter(AuthorizationCodeEntity.PARAM_HOST_UUID, hostUuid);
		query.setParameter(ClientDetailsEntity.PARAM_CLIENT_ID, clientId);
		return JpaUtil.getSingleResult(query.getResultList());
	}

	/* (non-Javadoc)
	 * @see org.mitre.oauth2.repository.OAuth2ClientRepository#saveClient(org.mitre.oauth2.model.ClientDetailsEntity)
	 */
	@Override
	public ClientDetailsEntity saveClient(String hostUuid, ClientDetailsEntity client) {
		return JpaUtil.saveOrUpdate(client.getClientId(), manager, client);
	}

	/* (non-Javadoc)
	 * @see org.mitre.oauth2.repository.OAuth2ClientRepository#deleteClient(org.mitre.oauth2.model.ClientDetailsEntity)
	 */
	@Override
	public void deleteClient(String hostUuid, ClientDetailsEntity client) {
		ClientDetailsEntity found = getById(hostUuid, client.getUuid());
		if (found != null) {
			manager.remove(found);
		} else {
			throw new IllegalArgumentException("Client not found: " + client);
		}
	}

	@Override
	public ClientDetailsEntity updateClient(String hostUuid, String uuid, ClientDetailsEntity client) {
		// sanity check
		client.setUuid(uuid);
		
		client.setHostUuid(hostUuid);

		return JpaUtil.saveOrUpdate(uuid, manager, client);
	}

	@Override
	public Collection<ClientDetailsEntity> getAllClients(String hostUuid) {
		TypedQuery<ClientDetailsEntity> query = manager.createNamedQuery(ClientDetailsEntity.QUERY_ALL, ClientDetailsEntity.class);
		query.setParameter(AuthorizationCodeEntity.PARAM_HOST_UUID, hostUuid);
		return query.getResultList();
	}

}
