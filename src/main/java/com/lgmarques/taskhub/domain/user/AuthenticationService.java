package com.lgmarques.taskhub.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }


    public Boolean register(UserData userData) {
        if (!isSafeInput(userData.name()) || !isSafeInput(userData.email()) || !isSafeInput(userData.password())) throw new RuntimeException("Invalid input");
        if (userRepository.existsByEmail(userData.email())) throw new RuntimeException("E-mail already registered");
        User user = new User();
        user.setEmail(userData.email());
        user.setName(userData.name());
        user.setPassword(passwordEncoder.encode(userData.password()));
        userRepository.save(user);
        return true;
    }

    public boolean isSafeInput(String input) {
        return !containsSqlInjection(input) && !containsXSS(input);
    }

    private boolean containsSqlInjection(String input) {
        // Implementação básica para verificar se há tentativa de injeção de SQL
        String[] sqlKeywords = {"SELECT", "UPDATE", "DELETE", "DROP", "INSERT", "ALTER", "FROM"};
        for (String keyword : sqlKeywords) {
            if (input.toUpperCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsXSS(String input) {
        // Implementação básica para verificar se há tentativa de ataque XSS
        String[] xssKeywords = {"<script>", "javascript:", "onerror", "onload", "alert("};
        for (String keyword : xssKeywords) {
            if (input.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
