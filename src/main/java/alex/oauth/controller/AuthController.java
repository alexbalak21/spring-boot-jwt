package alex.oauth.controller;

import alex.oauth.service.TokenService;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class AuthController {

    private final Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token")
    public String token(Authentication authentication) {
        LOG.debug("Authentication: {}", authentication.getName());
        LOG.debug("Toke granted: {}", authentication.getAuthorities());
        return tokenService.generateToken(authentication);
    }
}
