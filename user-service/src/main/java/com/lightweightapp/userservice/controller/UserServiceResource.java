package com.lightweightapp.userservice.controller;

import com.lightweightapp.userservice.configuration.RabbitMqMessagingConfig;
import com.lightweightapp.userservice.dbResource.User;
import com.lightweightapp.userservice.model.UserModel;
import com.lightweightapp.userservice.model.UserResponseModel;
import com.lightweightapp.userservice.model.UserVerificationModel;
import com.lightweightapp.userservice.repository.UserRepository;
import com.lightweightapp.userservice.service.UserRequestValidation;
import com.lightweightapp.userservice.util.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

@RestController
@RequestMapping("/user")
public class UserServiceResource {

    private UserRepository userRepository;

    public UserServiceResource(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/{userid}")
    public UserResponseModel getUser(@PathVariable("userid") final int userid)
    {
        User user = userRepository.findById(userid);
        UserResponseModel userResponseModel =  new UserResponseModel();
        userResponseModel.setRequestStatus(HttpStatus.OK.getReasonPhrase());
        userResponseModel.setResponseMessage("User record fetched successfully!");
        UserResponseModel.Data data = userResponseModel.new Data();
        data.setFirstName(user.getFirstName());
        data.setLastName(user.getLastName());
        data.setUserId(user.getId());
        data.setEmail(user.getEmail());
        userResponseModel.setData(data);
        return userResponseModel;
    }

    @PostMapping("/register")
    public UserResponseModel createUser(@RequestBody final UserModel user)
    {
        String requestValidationResponse = UserRequestValidation.validateUserRegRequest(user);
        if(requestValidationResponse == null)
        {
            User checkForDuplicateUser = userRepository.findByEmail(user.getEmail());
            if(checkForDuplicateUser == null)
            {
                int strength = 10; // work factor of bcrypt
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                /**
                 Save User records to the database
                 */
                User savedUserInfo = userRepository.save(new User(user.getFirstName(), user.getLastName(), user.getEmail(), encodedPassword, user.getStatus()));

                /**
                 * A verification email is sent to the user to verify it email credential. This verification payload is sent to the email service to consume and process
                 */

                UserVerificationModel userVerificationModel = new UserVerificationModel(savedUserInfo.getId(), savedUserInfo.getEmail());
                template.convertAndSend(RabbitMqMessagingConfig.EXCHANGE, RabbitMqMessagingConfig.ROUTING_KEY, userVerificationModel);

                /**
                 * Saved user data is mapped to a response model to present a response payload to the user making the request
                 */
                UserResponseModel userResponseModel = new UserResponseModel();
                userResponseModel.setResponseMessage("User created successfully");
                userResponseModel.setRequestStatus(HttpStatus.CREATED.getReasonPhrase());
                UserResponseModel.Data data = userResponseModel.new Data();
                data.setUserId(savedUserInfo.getId());
                data.setFirstName(savedUserInfo.getFirstName());
                data.setLastName(savedUserInfo.getLastName());
                data.setEmail(savedUserInfo.getEmail());
                userResponseModel.setData(data);
                return userResponseModel;
            }
            else
            {
                UserResponseModel userResponseModel = new UserResponseModel();
                userResponseModel.setResponseMessage("User already registered");
                userResponseModel.setRequestStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
                return userResponseModel;
            }

        }
        else
        {
            UserResponseModel userResponseModel = new UserResponseModel();
            userResponseModel.setResponseMessage(requestValidationResponse);
            userResponseModel.setRequestStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return userResponseModel;
        }

    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String key, @RequestBody String email,@RequestBody String password )
    {

        String token = jwtUtil.generateToken(key);
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

}
