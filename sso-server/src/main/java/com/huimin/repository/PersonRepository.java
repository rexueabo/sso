package com.huimin.repository;

import org.springframework.data.ldap.repository.LdapRepository;

import com.huimin.entity.Person;

public interface PersonRepository extends LdapRepository<Person>{

}
