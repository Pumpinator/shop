package com.shop.admin.controller;

import com.shop.admin.service.UserService;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;

    @GetMapping("/users/get_all")
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping("/users/check_email")
    public String checkEmail(@Param("id") Integer id, @Param("email") String email) {
        return userService.isEmailUnique(id, email) ? "Ok" : "Error";
    }
}
