package org.company.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.company.config.JwtService;
import org.company.domain.Users;
import org.company.dto.UserAccountDto;
import org.company.dto.UserDto;
import org.company.exp.BadRequestException;
import org.company.exp.ItemNotFoundException;
import org.company.form.LoginForm;
import org.company.form.UserForm;
import org.company.repository.UserRepository;
import org.company.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private void checkUsername(String username) {
        Optional<Users> optional = userRepository.findByUsernameAndStateIsTrue(username);
        if (optional.isPresent()) {
            throw new BadRequestException("This username is busy");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserForm form) {

        checkUsername(form.getUsername());

        return ResponseEntity.ok().body(service.saveUser(form));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginForm form) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));
        if (!auth.isAuthenticated()) {
            throw new ItemNotFoundException("Invalid user request");
        }

        UserDto dto = service.login(form);

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/profile")
    public ResponseEntity<UserAccountDto> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        String username = jwtService.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(service.getUser(username));
    }

}