package com.huimin.service;

import com.huimin.commen.LdapService;
import com.huimin.entity.Ou;

public interface OuService extends LdapService<Ou>{

	Ou create(String id, String name);
}
