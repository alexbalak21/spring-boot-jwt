package main.controllers;

import main.entities.User;
import main.services.JwtRefreshService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/refresh")
@RestController
public class RefreshController {

    private final JwtRefreshService jwtRefreshService;

    public RefreshController(JwtRefreshService jwtRefreshService) {
        this.jwtRefreshService = jwtRefreshService;
    }

    public String generateRefreshToken(User authenticatedUser) {
        return jwtRefreshService.generateRefreshToken(authenticatedUser);
    }

    @GetMapping("/renew")
    public String renewToken(String refreshToken) {

    }


}
