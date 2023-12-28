import org.example.GutenbergLibraryApplication;
import org.example.User;
import org.example.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {GutenbergLibraryApplication.class, TestEntityManager.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@Transactional
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repo;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("ravi@gmail.com");
        user.setPassword("alex2020");
        user.setFirstName("ravi");
        user.setLastName("Ravi");

        User savedUser = repo.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        String userEmail = user.getEmail();

        String existingUserEmail = existUser.getEmail();

        if (userEmail.equals(existingUserEmail)) {
            System.out.println("Test Passed: Email addresses are equal.");
        } else {
            System.out.println("Test Failed: Email addresses are not equal.");
        }
    }

    @Test
    public void testFindUserByEmail() {
        String email = "ravi@gmail.com";
        User user = repo.findByEmail(email);
        assertThat(user).isNotNull();
    }
}
