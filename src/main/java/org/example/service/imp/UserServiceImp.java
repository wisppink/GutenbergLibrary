package org.example.service.imp;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.UserDto;
import org.example.entity.LibBook;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    @Transactional
    public void updateUserLibrary(UserDto updatedUserDto) {
        // Check if the user with the provided ID exists
        Optional<User> optionalUser = userRepository.findById(updatedUserDto.getId());

        if (optionalUser.isPresent()) {
            // Update the user's library
            User existingUser = optionalUser.get();
            existingUser.setBooks(updatedUserDto.getBooks());

            // Save the updated user entity
            userRepository.save(existingUser);
        } else {
            // Handle the case where the user with the provided ID is not found
            throw new EntityNotFoundException("User not found with ID: " + updatedUserDto.getId());
        }
    }

    public Long getUserId(String email) {
        User user = userRepository.findByEmail(email);
        return (user != null) ? user.getId() : null;
    }

    @Override
    public void updateLastPageForBookInLibrary(String email, int bookId, int lastPage) {
        // Retrieve the user from the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }

        // Find the book in the user's library
        Optional<LibBook> optionalBook = user.getBooks().stream()
                .filter(book -> book.getId().equals(bookId))
                .findFirst();

        if (optionalBook.isPresent()) {
            // Update the last page for the specific book
            LibBook bookToUpdate = optionalBook.get();
            bookToUpdate.setLastPageIndex(lastPage);

            // Save the updated user entity
            userRepository.save(user);
        } else {
            // Handle the case where the book with the provided ID is not found in the user's library
            throw new EntityNotFoundException("Book not found in the user's library with ID: " + bookId);
        }
    }

}