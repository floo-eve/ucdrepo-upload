package ch.fad.ucd.ucdrepoupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.fad.ucd.ucdrepoupload.model.ComponentType;
import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;

/**
 * ApplicationComponentController
 */
@Controller
public class ApplicationComponentController {

    private UcdComponentService ucdComponentService;

    private ComponentType appType = new ComponentType("application");
    // private ComponentType mwType = new ComponentType("mw");

    public ApplicationComponentController(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    /**
     * Load the new component page.
     * 
     * @param model
     * @return
     */
    @GetMapping("/appcomponent/new")
    public String newComponent(Model model) {
        UcdComponent newcomp = new UcdComponent("", ucdComponentService.findComponentTypeDirectory(appType), appType);
        model.addAttribute("component", newcomp);
        return "appcomponentform";

    }

    /**
     * Create a new component
     * 
     * @param project
     * @param model
     * @return
     */
    @PostMapping("/appcomponent")
    public String createComponent(UcdComponent component, Model model) {
        System.out.println("Create component: " + component.getName());
        System.out.println("componenttype: " + component.getComponenttype().getType());
        ucdComponentService.save(component);
        return "redirect:/appcomponents";
    }

    /**
     * Get a Component by Name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/appcomponent/{name}")
    public String getComponentByName(@PathVariable String name, Model model) {
        UcdComponent component = ucdComponentService.findByName(name);
        if (component == null) {
            component = new UcdComponent();
        }
        model.addAttribute("component", component);
        return "appcomponent";
    }

    /**
     * Get all components.
     * 
     * @param model
     * @return
     */
    @GetMapping("/appcomponents")
    public String getComponents(Model model) {
        model.addAttribute("components", ucdComponentService.findAll());
        return "appcomponents";
    }

    /**
     * Load the edit component page for the component with the specified name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/appcomponent/edit/{name}")
    public String editComponent(@PathVariable String name, Model model) {
        UcdComponent component = ucdComponentService.findByName(name);
        if (component == null) {
            component = new UcdComponent();
        }
        model.addAttribute("component", component);
        return "appcomponentform";
    }

    /**
     * Update a component.
     * 
     * @param component
     * @return
     */
    @PostMapping("/appcomponent/{name}")
    public String updateComponent(UcdComponent component) {
        ucdComponentService.save(component);
        return "redirect:/appcomponent/" + component.getName();
    }

    /**
     * Delete a component.
     * 
     * @param component
     * @return
     */
    @PostMapping("/appcomponent/delete/{name}")
    public String deleteComponent(UcdComponent component) {
        ucdComponentService.delete(component);
        return "redirect:/appcomponents";
    }
}