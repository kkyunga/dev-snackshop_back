package org.back.devsnackshop_back.service;

import org.back.devsnackshop_back.entity.UserEntity;
import org.back.devsnackshop_back.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailAuthService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("해당 이메일 계정이 없어요"));
        return User.builder().username(user.getEmail()).password(user.getPasswordEncrypted()).roles("USER").build();
    }
}
