package com.srizvi.spring.service;


import com.srizvi.spring.domain.Role;
import com.srizvi.spring.domain.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName );
    User getUser(String username);
    List<User> getUsers();

}
