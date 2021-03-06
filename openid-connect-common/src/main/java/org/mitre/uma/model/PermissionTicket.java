/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
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

package org.mitre.uma.model;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * An UMA permission, used in the protection API.
 *
 * @author jricher
 *
 */
@Entity
@Table(name = "permission_ticket")
@NamedQueries({
	@NamedQuery(name = PermissionTicket.QUERY_TICKET, query = "select p from PermissionTicket p where p.hostUuid = :hostUuid and p.ticket = :" + PermissionTicket.PARAM_TICKET),
	@NamedQuery(name = PermissionTicket.QUERY_ALL, query = "select p from PermissionTicket p where p.hostUuid = :hostUuid"),
	@NamedQuery(name = PermissionTicket.QUERY_BY_RESOURCE_SET, query = "select p from PermissionTicket p where p.hostUuid = :hostUuid and p.permission.resourceSet.id = :" + PermissionTicket.PARAM_RESOURCE_SET_ID)
})
public class PermissionTicket {

	public static final String QUERY_TICKET = "PermissionTicket.queryByTicket";
	public static final String QUERY_ALL = "PermissionTicket.queryAll";
	public static final String QUERY_BY_RESOURCE_SET = "PermissionTicket.queryByResourceSet";

	public static final String PARAM_HOST_UUID = "hostUuid";
	public static final String PARAM_TICKET = "ticket";
	public static final String PARAM_RESOURCE_SET_ID = "rsid";

	private String id;
	private String hostUuid;
	private Permission permission;
	private String ticket;
	private Date expiration;
	private Collection<Claim> claimsSupplied;
	
	public PermissionTicket() {
		this.id = UUID.randomUUID().toString();
	}
	
	public PermissionTicket(String uuid) {
		this.id = uuid;
	}	
	
	@Id
	@Column(name = "uuid")
	public String getId() {
		return id;
	}

	public void setId(String uuid) {
		this.id = uuid;
	}

	@Basic
	@Column(name = "host_uuid")
	public String getHostUuid() {
		return hostUuid;
	}

	public void setHostUuid(String hostUuid) {
		this.hostUuid = hostUuid;
	}

	/**
	 * @return the permission
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "permission_uuid")
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	/**
	 * @return the ticket
	 */
	@Basic
	@Column(name = "ticket")
	public String getTicket() {
		return ticket;
	}

	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * @return the expiration
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiration")
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the claimsSupplied
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "claim_to_permission_ticket",
			joinColumns = @JoinColumn(name = "permission_ticket_id"),
			inverseJoinColumns = @JoinColumn(name = "claim_id")
			)
	public Collection<Claim> getClaimsSupplied() {
		return claimsSupplied;
	}

	/**
	 * @param claimsSupplied the claimsSupplied to set
	 */
	public void setClaimsSupplied(Collection<Claim> claimsSupplied) {
		this.claimsSupplied = claimsSupplied;
	}


}
