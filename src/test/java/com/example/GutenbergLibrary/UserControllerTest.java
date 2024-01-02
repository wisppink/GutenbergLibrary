package org.example.controller;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRegisterUser() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setName("testuser");
		// Mocking the userRepository save method
		when(userRepository.save(any(User.class))).thenReturn(user);

		// When
		User savedUser = userController.registerUser(user);

		// Then
		assertEquals(1L, savedUser.getId());
		assertEquals("testuser", savedUser.getName());
		verify(userRepository, times(1)).save(any(User.class));
	}
}
