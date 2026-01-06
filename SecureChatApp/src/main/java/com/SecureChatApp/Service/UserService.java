package com.SecureChatApp.Service;

import com.SecureChatApp.Dto.Response.PublicUserResponse;
import com.SecureChatApp.Model.User;

import java.util.List;

public interface UserService {
    List<PublicUserResponse> listAllExcept(Long currentUserId);
    List<PublicUserResponse> listContacts(Long currentUserId);
    String getPublicKey(Long userId);
}

