package com.huimin.entity;

import javax.naming.Name;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

@Entry(objectClasses = {"organizationalUnit"})
public class Ou {

	@Id
	private Name dn;
	
	@Attribute(name = "ou")
	private String ou;
	public Ou() {
	}
	public Ou(String id) {
		if (StringUtils.isNotBlank(id)) {
			Name dn = LdapNameBuilder.newInstance(id).build();
			this.dn = dn;
		}
	}
	public Name getDn() {
		return dn;
	}
	public void setDn(Name dn) {
		this.dn = dn;
	}
	public String getOu() {
		return ou;
	}
	public void setOu(String ou) {
		this.ou = ou;
	}
	
	
}
