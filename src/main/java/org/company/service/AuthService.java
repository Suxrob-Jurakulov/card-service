package org.company.service;


import org.company.dto.UserAccountDto;
import org.company.dto.UserDto;
import org.company.form.LoginForm;
import org.company.form.UserForm;

public interface AuthService {

    UserDto saveUser(UserForm form);

    UserDto login(LoginForm form);

    UserAccountDto getUser(String username);

}
