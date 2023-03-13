package com.lightweightapp.userservice.controller;

import com.lightweightapp.userservice.dbResource.User;
import com.lightweightapp.userservice.model.UserResponseModel;
import com.lightweightapp.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/rest/user-service")
public class UserServiceResource {

    private UserRepository userRepository;

    public UserServiceResource(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

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
}
