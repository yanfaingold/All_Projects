package com.yan.service;

import com.yan.model.User;

import java.util.List;

/**
 * Created by Yan on 16-Apr-17.
 */
public interface UserService {
    void addUser(User user);
    void removeUser(User user);
    List<User> getAllUsers();
    User getUser(String username);
}
