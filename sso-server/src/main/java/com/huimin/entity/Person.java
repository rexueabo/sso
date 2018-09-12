package com.huimin.entity;



import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

@Entry(base = "ou=Developer",objectClasses = {"inetOrgPerson"} )
public final class Person {

	 @Id
	 private Name dn;
	 
	 @Attribute(name="cn")
	// @DnAttribute(value = "cn" ,index = 1)
	 private String cn;
	 
	 @Attribute(name="sn")
	 private String sn;
	 @Attribute(name="userPassword")
	 private String userPassword;
	 @Attribute(name = "uid")
	 @DnAttribute(value = "uid",index = 0)
	 private String uid;
	 public Person(){
//		 Name dn = LdapNameBuilder.newInstance("uid=testAdd2,ou=Developer").build();
//				    ;
//				  this.dn = dn;
	 }
	 
	 /* getter */
	 public Name getDn() {
	  return dn;
	 }
	 
	 public String getCn() {
	  return cn;
	 }
	 
	 public String getSn() {
	  return sn;
	 }
	 
	 public String getUserPassword() {
	  return userPassword;
	 }
	 
	 /* setter */
	 public void setDn(Name dn) {
	  this.dn = dn;
	 }
	 
	 public void setCn(String cn) {
	  this.cn = cn;
	 }
	 
	 public void setSn(String sn) {
	  this.sn = sn;
	 }
	 
	 public void setUserPassword(String userPassword) {
	  this.userPassword = userPassword;
	 }
	 
	 public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "Person [dn=" + dn + ", cn=" + cn + ", sn=" + sn + ", userPassword=" + userPassword + ", uid=" + uid
				+ "]";
	}

	
}
