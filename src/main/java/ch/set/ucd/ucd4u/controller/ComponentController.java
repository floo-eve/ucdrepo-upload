package ch.set.ucd.ucd4u.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.set.ucd.ucd4u.model.UcdComponent;
import ch.set.ucd.ucd4u.services.UcdComponentService;
import lombok.extern.slf4j.Slf4j;

/**
 * ComponentController
 */
@Slf4j
@Controller
public class ComponentController {

    private UcdComponentService ucdComponentService;

    public ComponentController(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    /**
     * Load the new component page.
     * 
     * @param model
     * @return
     */
    @GetMapping("/component/{type}/new")
    public String newComponent(@PathVariable String type, Model model) {
        UcdComponent newcomp = new UcdComponent("", ucdComponentService.findComponentTypeDirectory(type), type);
        model.addAttribute("component", newcomp);
        log.debug("component new");
        return "componentform";

    }

    /**
     * Create a new component
     * 
     * @param project
     * @param model
     * @return
     */
    @PostMapping("/component/{type}")
    public String createComponent(@PathVariable String type, UcdComponent component, Model model) {
        System.out.println("Create component: " + component.getName());
        System.out.println("componenttype: " + component.getType());
        ucdComponentService.save(component);
        return "redirect:/component/{type}/list";
    }

    /**
     * Get a Component by Name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/component/{type}/{name}")
    public String getComponentByName(@PathVariable String type, @PathVariable String name, Model model) {
        UcdComponent component = ucdComponentService.findByName(name);
        if (component == null) {
            component = new UcdComponent();
        }

        // //Sort Versions
        // component.getVersions().sort(Comparator.comparing(UcdComponent::getName));
        log.debug("show component");
        model.addAttribute("component", component);
        return "component";
    }

    /**
     * Get all components.
     * 
     * @param model
     * @return
     */
    @GetMapping("/component/{type}/list")
    public String getComponents(@PathVariable String type, Model model) {
        List<UcdComponent> list = new ArrayList<UcdComponent>(ucdComponentService.findAllByType(type));
        list.sort(Comparator.comparing(UcdComponent::getName));

        model.addAttribute("components", list);
        model.addAttribute("type", type);
        log.debug("list components");
        return "components";
    }

    /**
     * Load the edit component page for the component with the specified name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/component/{type}/edit/{name}")
    public String editComponent(@PathVariable String type, @PathVariable String name, Model model) {
        UcdComponent component = ucdComponentService.findByName(name);
        if (component == null) {
            // TODO should throw exception?
            component = new UcdComponent();
            component.setType(type);

        }
        model.addAttribute("component", component);
        return "componentform";
    }

    /**
     * Update a component.
     * 
     * @param component
     * @return
     */
    @PostMapping("/component/{type}/{name}")
    public String updateComponent(@PathVariable String type, UcdComponent component) {
        ucdComponentService.save(component);
        return "redirect:/component/" + type + "/" + component.getName();
    }

    /**
     * Delete a component.
     * 
     * @param type component type
     * @param name component name
     * @return
     */
    @GetMapping("/component/{type}/delete/{name}")
    public String deleteComponent(@PathVariable String type, @PathVariable String name) {
        log.debug("delete component " + name);
        UcdComponent component = ucdComponentService.findByName(name);
        ucdComponentService.delete(component);
        return "redirect:/component/" + type + "/list";
    }
}