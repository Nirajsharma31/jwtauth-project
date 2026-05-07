package com.example.jwtauth.JWTAuth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwtauth.JWTAuth.Service.UserService;
import com.example.jwtauth.JWTAuth.entity.UserEntity;

@RestController
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<UserEntity> getUser(@RequestHeader("Authorization") String token) {
	    return this.userService.getUserEntity(token);
	}
}
