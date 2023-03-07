package com.lightweightapp.userservice.repository;

import com.lightweightapp.userservice.dbResource.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int userId);
}
