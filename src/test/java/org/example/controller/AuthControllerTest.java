package org.example.controller;

import org.example.dto.UserDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    // This method will run before each test method
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHome() {
        String result = authController.home();
        assertEquals("index", result);
    }

    @Test
    void testShowRegistrationForm() {
        String result = authController.showRegistrationForm(model);
        assertEquals("register", result);
    }

    @Test
    void testRegistration_Successful() {
        UserDto userDto = new UserDto(); // Set up your UserDto object here
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findUserByEmail(anyString())).thenReturn(null);

        String result = authController.registration(userDto, bindingResult, model);
        assertEquals("redirect:/register?success", result);

        verify(userService, times(1)).saveUser(userDto);
    }

    @Test
    void testRegistration_EmailExists() {
        UserDto userDto = new UserDto(); // Set up your UserDto object here
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findUserByEmail(anyString())).thenReturn(new User());

        String result = authController.registration(userDto, bindingResult, model);
        assertEquals("/register", result);

        verify(model, times(0)).addAttribute(anyString(), any());
        verify(userService, times(0)).saveUser(userDto);
    }

    @Test
    void testRegistration_WithErrors() {
        UserDto userDto = new UserDto(); // Set up your UserDto object here
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.registration(userDto, bindingResult, model);
        assertEquals("/register", result);

        verify(model, times(1)).addAttribute(eq("user"), any());
        verify(userService, times(0)).saveUser(userDto);
    }

    @Test
    void testUsers() {
        List<UserDto> userList = new ArrayList<>(); // Set up your list of UserDto objects here
        when(userService.findAllUsers()).thenReturn(userList);

        String result = authController.users(model);
        assertEquals("users", result);

        verify(model, times(1)).addAttribute(eq("users"), eq(userList));
    }

    @Test
    void testLogin() {
        String result = authController.login();
        assertEquals("/login", result);
    }
}
