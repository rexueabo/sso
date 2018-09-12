package com.huimin;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;

import com.huimin.entity.Ou;
import com.huimin.entity.Person;
import com.huimin.service.OuService;
import com.huimin.service.PersonService;
import com.huimin.util.HttpClientUtils;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApplicationTests {

	@Autowired
	private PersonService personService;
	
	@Autowired
	private LdapOperations ldapOperations;
	
	@Autowired
	private OuService ouService;
	@Test
	public void test03() {
		   Map<String, Object> map = new HashMap<>();
			map.put("id", 10);
			String doPost = HttpClientUtils.doPost("http://localhost:8081/test", map);
			System.out.println(doPost);
	}
	//动态修改枚举值
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test02() throws Exception {
		Ou ou = new Ou();
		Entry annotation = ou.getClass().getAnnotation(Entry.class);
		String[] objectClasses = annotation.objectClasses();
		System.out.println(objectClasses[0]);
		InvocationHandler h = Proxy.getInvocationHandler(annotation);
        // 获取 AnnotationInvocationHandler 的 memberValues 字段
        Field hField = h.getClass().getDeclaredField("memberValues");
        // 因为这个字段事 private final 修饰，所以要打开权限
        hField.setAccessible(true);
        // 获取 memberValues
        Map memberValues = (Map) hField.get(h);
        // 修改 value 属性值
        memberValues.put("objectClasses", new String[] {"aaa"});
        // 获取 foo 的 value 属性值
        String[] value = annotation.objectClasses();
        System.out.println(value[0]); // ddd
		ouService.create("ou=haha,ou=addOu", "haha");
	}
	@Test
	public void test01() {
		LdapQuery ldapQuery = LdapQueryBuilder.query().base(LdapNameBuilder.newInstance()
				.add("ou", "Developer")
				.add("uid", "testAdd4")
				.build())
				.filter(new EqualsFilter("cn", "lisi2"));
		ldapOperations.authenticate(ldapQuery , "admin");
	}
	@Test
	public void addOU() {
//		Ou ou = new Ou("OuTest");
//		ou.setOu("OuTest");;
//		ouRepository.save(ou);
		Iterable<Person> findAll = personService.findAll();
		findAll.forEach(action -> {
			System.out.println(action);
		});
	}
	@Test
	public void contextLoads() {
		System.out.println("------------------------");
		//List<String> list = ldapOperations.list("dc=micmiu,dc=com");
		//System.out.println(list);
		Person person = new Person();
		//Optional<Person> findById = personRepository.findById(person.getDn());
		 person.setCn("lisi");
		  person.setSn("zhuliang");
		  person.setUid("testAdd9");
		  person.setUserPassword("admin");
		  LdapQuery ldapQuery = LdapQueryBuilder.query().base("ou=Developer")
				  .where("cn").is("lisi");
		//ldapOperations.create(person);
		  Person save = personService.save(person);
		  System.out.println(save);
		  Optional<Person> findOne = personService.findOne(ldapQuery);
		  findOne.ifPresent(consumer -> {
		  });
          //System.out.println(findOne);
		 // ldapOperations.update(person);
		//  personRepository.delete(person);
		  personService.update(person);
		  personService.findAll(ldapQuery).forEach(action -> {
			  String userPassword = action.getUserPassword();
			  String[] split = userPassword.split(",");
			  byte[] bytes = new byte[split.length];
			  for (int i = 0; i < split.length; i++) {
				bytes[i] = Byte.valueOf(split[i]);
			}
			  action.setUserPassword(new String(bytes));
			  System.out.println(action);
		  });
	}

}
