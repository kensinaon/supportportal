package com.sinaon.supportportal.enumeration;

import static com.sinaon.supportportal.constant.Authority.*;

/**
 * @author ksinaon
 * @since Oct 26, 2021
 */
public enum Role {
	
	ROLE_USER(USER_AUTHORITIES),
	ROLE_HR(HR_AUTHORITIES),
	ROLE_MANAGER(MANAGER_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES),
	ROLE_SUPER_USER(SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;
	
	Role(String...authorities){
		this.authorities = authorities;
	}
	
	public String[] getAuthorities() {
		return authorities;
	}
}
