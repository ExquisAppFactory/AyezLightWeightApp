package com.lightweightapp.userservice.controller;

import com.lightweightapp.userservice.configuration.RabbitMqMessagingConfig;
import com.lightweightapp.userservice.dbResource.User;
import com.lightweightapp.userservice.model.UserAuthResponseModel;
import com.lightweightapp.userservice.model.UserModel;
import com.lightweightapp.userservice.model.UserResponseModel;
import com.lightweightapp.userservice.model.UserVerificationModel;
import com.lightweightapp.userservice.repository.UserRepository;
import com.lightweightapp.userservice.service.UserRequestValidation;
import com.lightweightapp.userservice.util.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

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

    @Value("${jwt.secret}")
    private String jwtSecret;

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
                 * A verification email is sent to the user to verify the email credential. This verification payload is sent to the email service to consume and process
                 */
                String validationToken = UUID.randomUUID().toString();

                UserVerificationModel userVerificationModel = new UserVerificationModel(savedUserInfo.getId(), savedUserInfo.getEmail(), savedUserInfo.getFirstName(), validationToken);
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

    @PostMapping("/login")
    public UserAuthResponseModel authUser(@RequestHeader(value="Authorization") String basicAuthData)
    {
        String requestValidationResponse = UserRequestValidation.validateBasicAuthRequest(basicAuthData);
        if(requestValidationResponse == null)
        {
            String credentials = basicAuthData.split(" ")[1];
            byte[] decodedCredentials = Base64.getDecoder().decode(credentials);
            String decodedCredentialstring = new String(decodedCredentials);
            String email = decodedCredentialstring.split(":")[0];
            String password = decodedCredentialstring.split(":")[1];

            User checkedForUser = userRepository.findByEmail(email);
            if(checkedForUser != null)
            {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                boolean ifPasswordMatches = bCryptPasswordEncoder.matches(password, checkedForUser.getPassword());

                String token = jwtUtil.generateToken(jwtSecret);

                UserAuthResponseModel userAuthResponseModel = new UserAuthResponseModel();
                userAuthResponseModel.setResponseMessage("User auth success");
                userAuthResponseModel.setRequestStatus(HttpStatus.OK.getReasonPhrase());
                UserAuthResponseModel.Data data = userAuthResponseModel.new Data();
                data.setToken(token);
                userAuthResponseModel.setData(data);
                return userAuthResponseModel;
            }
            else
            {
                UserAuthResponseModel userAuthResponseModel = new UserAuthResponseModel();
                userAuthResponseModel.setResponseMessage("Invalid Credentials! No such user found!");
                userAuthResponseModel.setRequestStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
                return userAuthResponseModel;
            }
        }
        else {
            UserAuthResponseModel userAuthResponseModel = new UserAuthResponseModel();
            userAuthResponseModel.setResponseMessage(requestValidationResponse);
            userAuthResponseModel.setRequestStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return userAuthResponseModel;
        }

    }

}
