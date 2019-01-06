package ch.fad.ucd.ucdrepoupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IndexController
 */
@Controller
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
