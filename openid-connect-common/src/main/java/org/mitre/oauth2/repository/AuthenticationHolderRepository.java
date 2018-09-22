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
package org.mitre.oauth2.repository;

import java.util.List;

import org.mitre.data.PageCriteria;
import org.mitre.oauth2.model.AuthenticationHolderEntity;

public interface AuthenticationHolderRepository {
	public List<AuthenticationHolderEntity> getAll(String hostUuid);

	public AuthenticationHolderEntity getById(String hostUuid, String uuid);

	public void remove(String hostUuid, AuthenticationHolderEntity a);

	public AuthenticationHolderEntity save(String hostUuid, AuthenticationHolderEntity a);

	public List<AuthenticationHolderEntity> getOrphanedAuthenticationHolders(String hostUuid);

	public List<AuthenticationHolderEntity> getOrphanedAuthenticationHolders(String hostUuid, PageCriteria pageCriteria);
}
