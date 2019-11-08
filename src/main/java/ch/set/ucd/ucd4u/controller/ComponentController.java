package ch.set.ucd.ucd4u.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.set.ucd.ucd4u.exception.ComponentExistsException;
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
    @GetMapping("/{homeBase}/type/{type}/component/new")
    public String newComponent(@PathVariable String homeBase, @PathVariable String type, Model model) {
        UcdComponent newcomp = new UcdComponent("", homeBase,
                ucdComponentService.findComponentTypeDirectory(homeBase, type), type);
        model.addAttribute("component", newcomp);
        log.debug("component new");
        return "componentform";

    }

    /**
     * Create/Update a component
     * 
     * @param project
     * @param model
     * @return
     */
    @PostMapping("/{homeBase}/type/{type}/component")
    public String createComponent(@PathVariable String homeBase, @PathVariable String type, UcdComponent component,
            Model model) {
        System.out.println("Create component: " + component.getName());
        System.out.println("componenttype: " + component.getType());
        System.out.println("componenthometype: " + component.getHomeBase());
        ucdComponentService.save(component);
        return "redirect:/{homeBase}/type/{type}/component/list";
    }

    /**
     * Get a Component by Name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/{homeBase}/type/{type}/component/{name}")
    public String getComponentByName(@PathVariable String homeBase, @PathVariable String type,
            @PathVariable String name, Model model) {
        UcdComponent component = ucdComponentService.findByName(homeBase, name);
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
    @GetMapping("/{homeBase}/type/{type}/component/list")
    public String getComponents(@PathVariable String homeBase, @PathVariable String type, Model model) {
        List<UcdComponent> list = new ArrayList<UcdComponent>(
                ucdComponentService.findAllComponentsByType(homeBase, type));
        list.sort(Comparator.comparing(UcdComponent::getName));

        model.addAttribute("components", list);
        model.addAttribute("type", type);
        model.addAttribute("homeBase", homeBase);
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
    @GetMapping("/{homeBase}/type/{type}/component/edit/{name}")
    public String editComponent(@PathVariable String homeBase, @PathVariable String type, @PathVariable String name,
            Model model) {
        UcdComponent component = ucdComponentService.findByName(homeBase, name);
        if (component == null) {
            // TODO should throw exception?
            component = new UcdComponent();
            component.setType(type);

        }
        model.addAttribute("component", component);
        return "componentformrename";
    }

    /**
     * Update a component.
     * 
     * @param component
     * @return
     */
    @PostMapping("/{homeBase}/type/{type}/component/{name}")
    public String updateComponent(@PathVariable String homeBase, @PathVariable String type, UcdComponent component) {
        ucdComponentService.save(component);
        return String.format("redirect:/%s/type/%s/component/%s", homeBase, type, component.getName());
    }

    /**
     * Rename a component.
     * 
     * @param component
     * @return
     */
    @PostMapping("/{homeBase}/type/{type}/component/rename/{oldname}")
    public String updateComponent(@PathVariable String homeBase, @PathVariable String type,
            @PathVariable String oldname, @RequestParam String name, Model model) {
        log.debug("rename " + oldname + " to " + name);
        UcdComponent component = ucdComponentService.findByName(homeBase, oldname);
        component.setName(name);

        try {
            component = ucdComponentService.rename(oldname, component);
        } catch (ComponentExistsException e) {

            model.addAttribute("error", "Component name " + name + " already exists");
            component.setName(oldname);
            model.addAttribute("component", component);
            return "componentformrename";
        }
        return String.format("redirect:/%s/type/%s/component/list", homeBase, type);
    }

    /**
     * Delete a component.
     * 
     * @param type component type
     * @param name component name
     * @return
     */
    @GetMapping("/{homeBase}/type/{type}/component/delete/{name}")
    public String deleteComponent(@PathVariable String homeBase, @PathVariable String type, @PathVariable String name) {
        log.debug("delete component " + name);
        UcdComponent component = ucdComponentService.findByName(homeBase, name);
        ucdComponentService.delete(component);

        return String.format("redirect:/%s/type/%s/component/list", homeBase, type);
    }
}