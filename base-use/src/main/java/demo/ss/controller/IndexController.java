package demo.ss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/noauth")
    public String noAuth() {
        return "noauth";
    }

    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    @RequestMapping("/logout.html")
    public String logout() {
        return "logout";
    }


    @RequestMapping("/error.html")
    public String error() {
        return "error";
    }
}
