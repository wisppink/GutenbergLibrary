package org.example.service;

import org.example.dto.UserDto;
import org.example.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void updateUserLibrary(UserDto updatedUserDto);

    Long getUserId(String authentication);

    void updateLastPageForBookInLibrary(String email, int bookId, int i);
}