package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.dto.RegisterDto;
import com.backend.ecommerce.dto.UpdateUserProfileRequest;
import com.backend.ecommerce.dto.UserProfileDto;

public interface UserService extends CrudService<RegisterDto, Integer> {

     void changePassword(String email, ChangePasswordRequest request);

     UserProfileDto getCurrentUserProfile(String email);

     UserProfileDto updateCurrentUserProfile(String email, UpdateUserProfileRequest request);

     void deleteCurrentUser(String email);
}
