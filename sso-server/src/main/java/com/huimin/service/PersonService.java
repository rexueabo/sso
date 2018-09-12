package com.huimin.service;

import com.huimin.authentication.Authentication;
import com.huimin.commen.LdapService;
import com.huimin.entity.Person;

public interface PersonService extends LdapService<Person>, Authentication{

 void update(Person person);
}
