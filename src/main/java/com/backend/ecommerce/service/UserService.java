package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.UserDto;

public interface UserService extends CrudService<UserDto, Integer> {

     void changePassword(String email, ChangePasswordRequest request);
}
