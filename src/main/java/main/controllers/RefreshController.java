package main.controllers;

import main.entities.User;
import main.services.JwtRefreshService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/renew")
    public String renewToken(@RequestBody String refreshToken) {
        return "Refresh Token Given: " + refreshToken;
    }


}
