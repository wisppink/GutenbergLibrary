package org.example.mapper;

import org.example.dto.UserDto;
import org.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto mapToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        String[] nameParts = user.getName().split(" ", 2);

        if (nameParts.length > 0) {
            userDto.setFirstName(nameParts[0]);
        }

        if (nameParts.length > 1) {
            userDto.setLastName(nameParts[1]);
        }
        userDto.setEmail(user.getEmail());
        return userDto;
    }

}

