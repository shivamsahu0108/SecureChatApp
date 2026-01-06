package com.SecureChatApp.Repository;

import com.SecureChatApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // == findByEmail ==
    Optional<User> findByEmail(String email);

    // == listAllExceptUser ==
    @Query("""
        SELECT u FROM User u
        WHERE u.id <> :userId
    """)
    List<User> findAllExcept(Long userId);

    // == getPublicKeyById ==
    @Query("""
        SELECT u.publicKey FROM User u
        WHERE u.id = :userId
    """)
    Optional<String> findPublicKeyById(Long userId);

    // == listContactsForUser ==
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN Message m
          ON (u.id = m.senderId OR u.id = m.receiverId)
        WHERE u.id <> :userId
          AND (m.senderId = :userId OR m.receiverId = :userId)
    """)
    List<User> findContactsForUser(Long userId);
}
