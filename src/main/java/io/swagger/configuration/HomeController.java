package io.swagger.configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.Entity;


/**
 * Home redirection to swagger api documentation 
 */
@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index() {
        System.out.println("swagger-ui.html");
        return "redirect:swagger-ui.html";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity test() {
        return ResponseEntity.ok("Hello world");
    }
}

