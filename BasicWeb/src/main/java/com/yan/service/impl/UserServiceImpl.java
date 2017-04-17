package com.yan.service.impl;

import com.yan.model.User;
import com.yan.service.UserService;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Yan on 16-Apr-17.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void addUser(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Transactional
    public void removeUser(User user) {
        sessionFactory.getCurrentSession().delete(user);
    }

    @Transactional
    public List<User> getAllUsers() {
        List<User> users = null;
        try {
            users = sessionFactory.getCurrentSession().createQuery("from User").list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Transactional
    public User getUser(String username) {
        User result = null;
        try {
            Query query = sessionFactory.getCurrentSession().createQuery("select u from User as u where u.userName=:username");
            query.setParameter("username", username);
            result = (User) query.uniqueResult();
        } catch (Exception e) {
        }
        return result;
    }
}
