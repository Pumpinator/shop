package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.shop.admin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.shop.common.entity.Role;
import com.shop.common.entity.User;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateUserWithOneRole() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User userAlejandro = new User("alejandrodcardona5@hotmail.com", bCryptPasswordEncoder.encode("alejandro2023"), "Alejandro", "Delgado Cardona");
        userAlejandro.addRole(roleAdmin);
        userAlejandro.setPhoto("alejandro.jpg");
        userAlejandro.setEnabled(true);
        User savedUser = userRepository.save(userAlejandro);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateTwoUsers() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User userAlejandra = new User("aledelrociocar@hotmail.com", bCryptPasswordEncoder.encode("alejandra2023"), "Alejandra Del Rocío", "Cardona Guerra");
        User userMelissa = new User("mely.dcardona@gmail.com", bCryptPasswordEncoder.encode("melissa2023"), "Melissa", "Delgado Cardona");
        userAlejandra.addRole(roleAdmin);
        userMelissa.addRole(roleAdmin);
        userAlejandra.setPhoto("alejandra.jpg");
        userMelissa.setPhoto("melissa.jpg");
        User savedUserAlejandra = userRepository.save(userAlejandra);
        User savedUserMelissa = userRepository.save(userMelissa);
        assertThat(savedUserAlejandra.getId()).isGreaterThan(0);
        assertThat(savedUserMelissa.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRoles() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        User userPaulina = new User("paulinaduran3e@gmail.com", bCryptPasswordEncoder.encode("paulina2023"), "Ana Paulina", "Durán Martínez");
        userPaulina.addRole(roleEditor);
        userPaulina.addRole(roleAssistant);
        userPaulina.setPhoto("paulina.jpg");
        User savedUser = userRepository.save(userPaulina);
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
    public void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> users = page.getContent();
        users.forEach(user -> {
            System.out.println(user);
        });
        assertThat(users.size()).isEqualTo(pageSize);
    }

    @Test
    public void testListSecondPage() {
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> users = page.getContent();
        users.forEach(user -> {
            System.out.println(user);
        });
        assertThat(users.size()).isEqualTo(pageSize);
    }

    @Test
    public void testGetAllUsersByKeyword() {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAllByKeyword(keyword, pageable);
        List<User> users = page.getContent();
        users.forEach(user -> {
            System.out.println(user);
        });
        assertThat(users.size()).isGreaterThan(0);
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

    @Test
    public void testEncodeUserPassword() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findById(3).get();
        String rawPassword = user.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        userRepository.save(user);
        System.out.println(encodedPassword);
        boolean matches = bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
        assertThat(matches).isTrue();
    }
}
