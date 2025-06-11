package main.controllers;

import main.entities.User;
import main.dtos.LoginUserDto;
import main.dtos.RegisterUserDto;
import main.responses.LoginResponse;
import main.services.AuthenticationService;
import main.services.JwtRefreshService;
import main.services.JwtService;
import main.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final JwtRefreshService jwtRefreshService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserService userService, JwtRefreshService jwtRefreshService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.jwtRefreshService = jwtRefreshService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtRefreshService.generateRefreshToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setRefreshExpiresIn(jwtService.getRefreshExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}