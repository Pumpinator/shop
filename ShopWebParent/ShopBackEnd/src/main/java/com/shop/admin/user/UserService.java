package com.shop.admin.user;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    public List<Role> getRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
