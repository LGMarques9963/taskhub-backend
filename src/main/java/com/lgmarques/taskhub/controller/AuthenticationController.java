package com.lgmarques.taskhub.controller;

import com.lgmarques.taskhub.domain.user.AuthenticationData;
import com.lgmarques.taskhub.domain.user.User;
import com.lgmarques.taskhub.domain.user.UserData;
import com.lgmarques.taskhub.domain.user.UserRepository;
import com.lgmarques.taskhub.infra.security.TokenData;
import com.lgmarques.taskhub.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenData> authenticate(@RequestBody @Valid AuthenticationData authenticationData) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationData.email(), authenticationData.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenData(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserData userData) {
        if (userRepository.existsByEmail(userData.email())) {
            return ResponseEntity.badRequest().body("E-mail already registered");
        }
        User user = new User();
        user.setEmail(userData.email());
        user.setName(userData.name());
        user.setPassword(passwordEncoder.encode(userData.password()));
        userRepository.save(user);
        return ResponseEntity.ok().body("User created");
    }
}
