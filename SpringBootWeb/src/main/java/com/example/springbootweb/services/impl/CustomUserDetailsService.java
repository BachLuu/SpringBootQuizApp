package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.services.interfaces.ICustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ICustomUserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                var user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                ErrorMessage.USER_NOT_FOUND + ": " + email));

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .disabled(!user.getIsActive())
                                .authorities(user.getRoles()
                                                .stream()
                                                .map(role -> new SimpleGrantedAuthority(
                                                                "ROLE_" + role.getName().toUpperCase()))
                                                .toList())
                                .build();
        }
}
