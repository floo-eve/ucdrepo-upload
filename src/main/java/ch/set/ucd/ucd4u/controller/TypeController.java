package ch.set.ucd.ucd4u.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.set.ucd.ucd4u.services.UcdComponentService;
import lombok.extern.slf4j.Slf4j;

/**
 * TypeController
 */
@Slf4j
@Controller
public class TypeController {

    private UcdComponentService ucdComponentService;

    public TypeController(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    /**
     * Get all types.
     * 
     * @param String homeBase of the repo
     * @param model
     * @return String repotypes template
     */
    @GetMapping("/{homeBase}/type/list")
    public String getRepoTypes(@PathVariable String homeBase, Model model) {
        List<String> list = new ArrayList<String>(ucdComponentService.findAllTypes(homeBase));

        model.addAttribute("types", list);
        log.debug("list types");
        return "repotypes";
    }

}