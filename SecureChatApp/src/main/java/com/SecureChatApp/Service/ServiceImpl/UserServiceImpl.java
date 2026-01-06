package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Dto.Response.PublicUserResponse;
import com.SecureChatApp.Exception.ResourceNotFoundException;
import com.SecureChatApp.Model.User;
import com.SecureChatApp.Repository.UserRepository;
import com.SecureChatApp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository usersRepository;

    /* -------------------------------
     * listAllUsers
     * ------------------------------- */
    @Override
    public List<PublicUserResponse> listAllExcept(Long currentUserId) {
        return usersRepository.findAllExcept(currentUserId)
                .stream()
                .map(u -> PublicUserResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .publicKey(u.getPublicKey())
                        .build()
                )
                .toList();
    }

    @Override
    public List<PublicUserResponse> listContacts(Long currentUserId) {
        return usersRepository.findContactsForUser(currentUserId)
                .stream()
                .map(u -> PublicUserResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .publicKey(u.getPublicKey())
                        .build()
                )
                .toList();
    }


    /* -------------------------------
     * getPublicKey
     * ------------------------------- */
    @Override
    public String getPublicKey(Long userId) {
        return usersRepository.findPublicKeyById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }


}
