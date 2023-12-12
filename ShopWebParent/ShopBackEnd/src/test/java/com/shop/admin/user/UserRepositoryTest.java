package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testCreateUser() {
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User userAlejandro = new User("alejandro@loutrecode.net", "alejandro2304", "Alejandro", "Delgado Cardona");
        userAlejandro.addRole(roleAdmin);
        User savedUser = userRepository.save(userAlejandro);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
}
