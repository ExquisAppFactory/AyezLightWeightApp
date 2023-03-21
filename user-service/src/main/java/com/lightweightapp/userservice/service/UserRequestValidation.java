package com.lightweightapp.userservice.service;

import com.lightweightapp.userservice.dbResource.User;
import com.lightweightapp.userservice.model.UserModel;

public class UserRequestValidation {

    public static String validateUserRegRequest(UserModel user)
    {
        String responseErr = null;
        if(user.getEmail() == null || user.getEmail().isEmpty())
            responseErr = "User email is required";
        else if(user.getFirstName() == null || user.getFirstName().isEmpty())
            responseErr = "User first name is required";
        else if(user.getLastName() == null || user.getLastName().isEmpty())
            responseErr = "User last name is required";
        else if(user.getPassword() == null || user.getPassword().isEmpty())
            responseErr = "User password is required";
        else if(user.getStatus() == null || user.getStatus().isEmpty())
            responseErr = "User status is required";
        else if(user.getStatus() != null || !user.getStatus().isEmpty())
        {
            if(!user.getStatus().equals(User.IN_ACTIVE_USER))
            {
                responseErr = "User status is invalid";
            }
        }
        return responseErr;
    }
}
