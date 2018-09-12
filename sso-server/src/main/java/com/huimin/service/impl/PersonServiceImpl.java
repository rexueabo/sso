package com.huimin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import com.huimin.entity.Person;
import com.huimin.repository.PersonRepository;
import com.huimin.service.PersonService;
import com.huimin.util.LogUtil;

@Service
public class PersonServiceImpl implements PersonService{

	private static LogUtil logger = LogUtil.logger(PersonServiceImpl.class);
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private LdapOperations ldapOperations;
	@Override
	public LdapRepository<Person> ldapRepository() {
		return personRepository;
	}
	@Override
	public void update(Person person) {
		ldapOperations.update(person);
	}
	@Override
	public boolean authenticate(String userName, String passWord) {
		LdapQuery ldapQuery =LdapQueryBuilder.query().base("ou=Developer")
				.filter(new EqualsFilter("uid", userName));
		try {
			ldapOperations.authenticate(ldapQuery, passWord);
			return true;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

}
