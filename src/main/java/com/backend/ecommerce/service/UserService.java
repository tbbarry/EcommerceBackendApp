package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.RegisterDto;

public interface UserService extends CrudService<RegisterDto, Integer> {

     void changePassword(String email, ChangePasswordRequest request);
}
