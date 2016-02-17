/*******************************************************************************
 *
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.woo.jdbcx.modal;

import java.util.Date;
import java.util.Map;

public class MemberForPostgres {

	private Integer id;

	private String name;

	private String registIp;

	private Date createdOn;

	private Date updatedOn;

	private Boolean isAdmin;


	//	private List<Integer> profile;
	private Map<String, Object> profile;
	//	private String profile;

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return regist_ip
	 */
	public String getRegistIp() {
		return registIp;
	}

	/**
	 * @param registIp
	 */
	public void setRegistIp(String registIp) {
		this.registIp = registIp;
	}

	/**
	 * @return created_on
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return updated_on
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return is_admin
	 */
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin
	 */
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}


	/**
	 * @return the profile
	 */
	public Map<String, Object> getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Member [id=" + id + ", name=" + name + ", registIp=" + registIp + ", createdOn=" + createdOn
				+ ", updatedOn=" + updatedOn + ", isAdmin=" + isAdmin + ", profile=" + profile + "]";
	}

}