package com.yan.model;

import javax.persistence.*;

/**
 * Created by Yan on 16-Apr-17.
 */
@Entity(name = "User")
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false, unique = true)
    private String userName;
    @Column(nullable = false)
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User(){}
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
