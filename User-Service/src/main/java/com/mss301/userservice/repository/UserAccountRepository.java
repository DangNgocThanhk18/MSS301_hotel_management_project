package com.mss301.userservice.repository;

import com.mss301.userservice.pojos.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}