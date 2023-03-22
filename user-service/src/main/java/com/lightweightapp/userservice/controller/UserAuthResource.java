package com.lightweightapp.userservice.controller;

import com.lightweightapp.userservice.repository.UserRepository;
import com.lightweightapp.userservice.util.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserAuthResource {

    private UserRepository userRepository;

    public UserAuthResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String userName) {
        String token = jwtUtil.generateToken(userName);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }
}
