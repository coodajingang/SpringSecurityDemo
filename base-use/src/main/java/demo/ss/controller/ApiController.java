package demo.ss.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @RequestMapping("/auth")
    public String getAuthentication() {
        return "auth api response";
    }

    @RequestMapping("/noauth")
    public String noauth() {
        return "no auth api response";
    }
}
