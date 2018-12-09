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
package org.mitre.openid.connect.repository.impl;

import static org.mitre.util.jpa.JpaUtil.getSingleResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.mitre.host.service.HostInfoService;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * JPA UserInfo repository implementation
 *
 * @author Michael Joseph Walsh
 * @author Nasim Kabiliravi
 *
 */
@Repository("jpaUserInfoRepository")
public class JpaUserInfoRepository implements UserInfoRepository {

	@PersistenceContext(unitName="defaultPersistenceUnit")
	private EntityManager manager;

	@Autowired
	HostInfoService hostInfoService;
	
	/**
	 * Get a single UserInfo object by its username for a specific host
	 */
	@Override
	public UserInfo getByUuid(String uuid) {
		DefaultUserInfo entity = manager.find(DefaultUserInfo.class, uuid);
		if (entity == null) {
			throw new IllegalArgumentException("ApprovedSite not found: " + uuid);
		}
		hostInfoService.validateHost(entity.getHostUuid());
		return entity;
	}

	/**
	 * Get a single UserInfo object by its email address for a specific host
	 */
	@Override
	public UserInfo getByEmailAddress(String email) {
		TypedQuery<DefaultUserInfo> query = manager.createNamedQuery(DefaultUserInfo.QUERY_BY_EMAIL, DefaultUserInfo.class);
		query.setParameter(DefaultUserInfo.PARAM_HOST_UUID, hostInfoService.getCurrentHostUuid());
		query.setParameter(DefaultUserInfo.PARAM_EMAIL, email);

		return getSingleResult(query.getResultList());
	}

}
