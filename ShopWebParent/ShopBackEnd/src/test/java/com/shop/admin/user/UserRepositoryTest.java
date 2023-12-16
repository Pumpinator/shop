package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.shop.admin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User userAlejandro = new User("eviladc@hotmail.com", "alejandro2023", "Alejandro", "Delgado Cardona");
        userAlejandro.addRole(roleAdmin);
        User savedUser = userRepository.save(userAlejandro);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRoles() {
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        User userAlejandra = new User("aledelrociocar@hotmail.com", "alejandra2023", "Alejandra Del Rocío", "Cardona Guerra");
        userAlejandra.addRole(roleEditor);
        userAlejandra.addRole(roleAssistant);
        User savedUser = userRepository.save(userAlejandra);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetUserById() {
        User userAlejandro = userRepository.findById(1).get();
        System.out.println(userAlejandro);
        assertThat(userAlejandro.getId()).isNotNull();
    }

    @Test
    public void testGetUserByEmail() {
        String email = "eviladc@hotmail.com";
        User user = userRepository.findUserByEmail(email);
        assertThat(user).isNotNull();
    }

    @Test
    public void testGetAllUsers() {
        Iterable<User> users = userRepository.findAll();
        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void testUpdateUser() {
        User userAlejandro = userRepository.findById(1).get();
        userAlejandro.setEnabled(true);
        userAlejandro.setEmail("eviladc@gmail.com");
        userRepository.save(userAlejandro);
    }

    @Test
    public void testUpdateUserRoles() {
        User userAlejandra = userRepository.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);
        userAlejandra.getRoles().remove(roleEditor);
        userAlejandra.addRole(roleSalesperson);
        userRepository.save(userAlejandra);
    }

    @Test
    public void testUpdateUserEnabled() {
        Integer id = 1;
        userRepository.updateUserStatus(id, true);
    }

    @Test
    public void testDeleteUserById() {
        Integer userId = 2;
        userRepository.deleteById(userId);
    }

    @Test
    public void testCountUserById() {
        Integer id = 1;
        Long count = userRepository.countUserById(id);
        assertThat(count).isNotNull().isGreaterThan(0);
    }
}
