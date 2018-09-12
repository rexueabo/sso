package com.huimin.commen;

import java.util.Optional;

import javax.naming.Name;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.ldap.query.LdapQuery;


public interface LdapService <T> {
	LdapRepository<T> ldapRepository();
	default <S extends T> S save(S entity) {
		return ldapRepository().save(entity);
	};

	default <S extends T> Iterable<S> saveAll(Iterable<S> entities){
		return ldapRepository().saveAll(entities);
	};

	default Optional<T> findById(Name id){
		return ldapRepository().findById(id);
	}

	default boolean existsById(Name id) {
		return ldapRepository().existsById(id);
	}

	default Iterable<T> findAll(){
		return ldapRepository().findAll();
	}

	default Iterable<T> findAllById(Iterable<Name> ids){
		return ldapRepository().findAllById(ids);
	}
	default long count() {
		return ldapRepository().count();
	}

	default void deleteById(Name id) {
		ldapRepository().deleteById(id);
	}
	default void delete(T entity) {
		ldapRepository().delete(entity);
	}

	default void deleteAll(Iterable<? extends T> entities) {
		ldapRepository().deleteAll(entities);
	}
	default void deleteAll() {
		ldapRepository().deleteAll();
	}
	default Optional<T> findOne(LdapQuery ldapQuery){
		return ldapRepository().findOne(ldapQuery);
	}
	default Iterable<T> findAll(LdapQuery ldapQuery){
		return ldapRepository().findAll(ldapQuery);
	}
}
