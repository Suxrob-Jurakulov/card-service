package org.company.service;

import lombok.RequiredArgsConstructor;
import org.company.config.JwtService;
import org.company.domain.Role;
import org.company.domain.Users;
import org.company.dto.UserAccountDto;
import org.company.dto.UserDto;
import org.company.form.LoginForm;
import org.company.form.UserForm;
import org.company.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto saveUser(UserForm form) {
        Users entity = new Users();

        entity.setUsername(form.getUsername());
        entity.setPassword(passwordEncoder.encode(form.getPassword()));
        entity.setRole(Role.ROLE_USER);
        entity.setCreateAt(LocalDateTime.now());
        entity.setState(true);

        repository.save(entity);
        return map(entity);
    }


    @Override
    public UserDto login(LoginForm form) {
        String jwt = jwtService.generateToken(form.getUsername());

        UserDto dto = new UserDto();
        dto.setUsername(form.getUsername());
        dto.setJwt(jwt);
        return dto;
    }

    @Override
    public UserAccountDto getUser(String username) {
        Optional<Users> opt = repository.findByUsernameAndStateIsTrue(username);
        if (opt.isPresent()) {
            Users entity = opt.get();

            UserAccountDto dto = new UserAccountDto();

            dto.setId(entity.getId());
            dto.setUsername(entity.getUsername());
            dto.setCreatedAt(entity.getCreateAt());
            return dto;
        }
        return null;
    }


    // MAPPER
    private UserDto map(Users entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setCreatedAt(entity.getCreateAt());
        return dto;
    }

}