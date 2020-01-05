package com.example.demo;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.BusinessObject;
import com.example.demo.model.User;
import com.example.demo.repo.BusinessObjectRepository;
import com.example.demo.repo.SecureBusinessObjectRepository;
import com.example.demo.repo.UserRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class KievSecurityJpaApplicationTests {
	@Autowired UserRepository userRepository;
	@Autowired BusinessObjectRepository businessObjectRepository;
	@Autowired SecureBusinessObjectRepository secureBusinessObjectRepository;
	User tom, ollie, admin;
	BusinessObject object1, object2, object3;
	UsernamePasswordAuthenticationToken olliAuth, tomAuth, adminAuth;
	
	@Before
	public void setup() {
		// it is not called for unknown reason.
		System.out.println("setup");
	}


	@Test
	public void contextLoads() {
		System.out.println("contextLoads");
		tom = userRepository.save(new User("thomas", "darimont", "tdarimont@example.org"));
		ollie = userRepository.save(new User("oliver", "gierke", "ogierke@example.org"));
		admin = userRepository.save(new User("admin", "admin", "admin@example.org"));
		long count = userRepository.count();
		assertEquals(3,count);
		object1 = businessObjectRepository.save(new BusinessObject("object1", ollie));
		object2 = businessObjectRepository.save(new BusinessObject("object2", ollie));
		object3 = businessObjectRepository.save(new BusinessObject("object3", tom));
		count = businessObjectRepository.count();
		assertEquals(3,count);
		
		olliAuth = new UsernamePasswordAuthenticationToken(ollie, "x");
		tomAuth = new UsernamePasswordAuthenticationToken(tom, "x");
		adminAuth = new UsernamePasswordAuthenticationToken(admin, "x",
				singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
		
		SecurityContextHolder.getContext().setAuthentication(tomAuth);		
		List<BusinessObject> businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
		assertThat(businessObjects, hasSize(1));
		assertThat(businessObjects, contains(object3));
		
		SecurityContextHolder.getContext().setAuthentication(olliAuth);
		businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
		assertThat(businessObjects, hasSize(2));
		assertThat(businessObjects, contains(object1, object2));
		
		SecurityContextHolder.getContext().setAuthentication(adminAuth);
		businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUser();
		assertThat(businessObjects, hasSize(3));
		assertThat(businessObjects, contains(object1, object2, object3));
		
		SecurityContextHolder.getContext().setAuthentication(adminAuth);
		businessObjects = secureBusinessObjectRepository.findBusinessObjectsForCurrentUserById();
		assertThat(businessObjects, hasSize(3));
		assertThat(businessObjects, contains(object1, object2, object3));
		
		secureBusinessObjectRepository.modifiyDataWithRecordingSecurityContext();
		for (BusinessObject bo : businessObjectRepository.findAll()) {
			assertThat(bo.getLastModifiedDate(), is(notNullValue()));
			assertThat(bo.getLastModifiedBy().getFirstname(), is("admin"));
		}
	}

}
