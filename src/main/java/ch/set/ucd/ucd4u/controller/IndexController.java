package ch.set.ucd.ucd4u.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * IndexController
 */
@Controller
@Slf4j
public class IndexController {

    @RequestMapping({ "", "/", "index", "index.html" })
    public String index() {
        return "index";
    }

    @RequestMapping({ "login" })
    public String login() {

        return "login";
    }

    @RequestMapping("/oups")
    public String oupsHandler() {
        return "notimplemented";
    }
}
