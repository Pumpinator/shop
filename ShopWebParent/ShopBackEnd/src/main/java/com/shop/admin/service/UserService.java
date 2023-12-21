package com.shop.admin.service;

import com.shop.admin.configuration.security.ShopUserDetails;
import com.shop.admin.exception.UserNotFoundException;
import com.shop.admin.repository.RoleRepository;
import com.shop.admin.repository.UserRepository;
import com.shop.common.entity.Role;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new UserNotFoundException("Could not find any user with id " + id);
        }
    }

    public List<User> getAll() {
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public List<Role> getRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public Page<User> paginate(int pageNumber, String sortField, String sortOrder, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        if (keyword != null) return userRepository.findAllByKeyword(keyword, pageable);
        return userRepository.findAll(pageable);
    }

    public User save(User user) {
        boolean isUpdatingUser = user.getId() != null;
        if (isUpdatingUser) {
            User updatedUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public void updateStatus(Integer id, boolean enabled) {
        userRepository.updateUserStatus(id, enabled);
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countUserById = userRepository.countUserById(id);
        if (countUserById == null || countUserById == 0) {
            throw new UserNotFoundException("Could not find any user with id " + id);
        }
        userRepository.deleteById(id);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (user != null) return false;
        } else {
            if (user.getId() != id) {
                return false;
            }
        }
        return true;
    }
}
