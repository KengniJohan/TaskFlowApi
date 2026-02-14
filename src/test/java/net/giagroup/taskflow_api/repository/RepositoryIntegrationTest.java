package net.giagroup.taskflow_api.repository;

import net.giagroup.taskflow_api.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void canSaveAndFindUser() {
        User u = new User();
        u.setUsername("testuser");
        u.setEmail("test@example.com");
        u.setPassword("secret");
        userRepository.save(u);

        var found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}
