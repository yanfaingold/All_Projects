package com.yan.controller;

import com.yan.model.User;
import com.yan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Yan on 26-Mar-17.
 */
@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(value = "/AllUsers")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "show_users";
    }

    @RequestMapping(value = "/AddUser")
    public
    @ResponseBody
    void addUser(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password, HttpServletResponse response) throws Exception {
        String responseText = "";
        boolean error = false;
        if (isEmpty(userName) || isEmpty(password)) {
            responseText = "Please fill all fields";
            error = true;
        } else if (userService.getUser(userName) != null) {
            responseText = "User name already exists";
            error = true;
        }
        if (!error) {
            userService.addUser(new User(userName, password));
            responseText = "User added";
        }
        response.getWriter().append(responseText);
        if(error)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.flushBuffer();
    }

    private boolean isEmpty(String str) {
        boolean isEmpty = true;
        if (str != null && !str.equals("")) {
            isEmpty = false;
        }
        return isEmpty;
    }
}
