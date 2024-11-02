package alex.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("")
    public String publicHome() {
        return  "Public Home";
    }

    @GetMapping("/page")
    public String page(){
        return "Public Page";
    }
}
