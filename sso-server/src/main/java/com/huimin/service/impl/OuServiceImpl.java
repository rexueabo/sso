package com.huimin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.stereotype.Service;

import com.huimin.entity.Ou;
import com.huimin.repository.OuRepository;
import com.huimin.service.OuService;

@Service
public class OuServiceImpl implements OuService{

	@Autowired
	private OuRepository ouRepository;
	@Autowired
	private LdapOperations ldapOperations;
	@Override
	public LdapRepository<Ou> ldapRepository() {
		return ouRepository;
	}

	@Override
	public Ou create(String id, String name) {
		Ou ou = new Ou(id);
		ou.setOu(name);
		ldapOperations.create(ou);
		return ou;
	}
}
