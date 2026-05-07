package com.example.jwtauth.JWTAuth.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.jwtauth.JWTAuth.AuthEntryPoint.JWTHelper;
import com.example.jwtauth.JWTAuth.entity.UserEntity;
import org.springframework.web.bind.annotation.RestController;

@Service
public class UserService {

	private List<UserEntity> store = new ArrayList<>();


	@Autowired
	private JWTHelper helper;
	
	 @Autowired
	    private UserDetailsService userDetailsService;

	public UserService()
	{
		store.add(new UserEntity(UUID.randomUUID().toString(), "Niraj", "Niraj@gmai.com"));
		store.add(new UserEntity(UUID.randomUUID().toString(), "Demo", "Nidfraj@gmai.com"));
		store.add(new UserEntity(UUID.randomUUID().toString(), "TEst", "Nirdfdfdfdaj@gmai.com"));
		store.add(new UserEntity(UUID.randomUUID().toString(), "DemoTestdfsdf", "Nidfdfraj@gmai.com"));
		
	}

	public List<UserEntity> getUserEntity(String token) {
		 if (token.startsWith("Bearer ")) {
		        token = token.substring(7);
		    }
		 String username = helper.getUsernameFromToken(token);
		 UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		 if(helper.validateToken(token, userDetails))
		return this.store;
	else
	{
		System.out.println("Token expired or invalid");
		throw new RuntimeException("Token expired or invalid");
	}
	}
}
