package com.mss301.userservice.service;

import com.mss301.userservice.pojos.UserAccount;
import com.mss301.userservice.repository.UserAccountRepository;
import com.mss301.userservice.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByUsernameOrEmail(loginInput, loginInput)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với: " + loginInput));

        return new CustomUserDetails(user);
    }
}