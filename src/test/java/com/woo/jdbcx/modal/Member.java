package com.woo.jdbcx.modal;

import java.util.Date;

public class Member {
    private Integer id;

    private String name;

    private String registIp;

    private Date createdOn;

    private Date updatedOn;

    private Boolean isAdmin;

	private Object profile;

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
     * @return profile
     */
	public Object getProfile() {
        return profile;
    }

    /**
     * @param profile
     */
	public void setProfile(Object profile) {
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