package ch.fad.ucd.ucdrepoupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;

/**
 * VersionController
 */
@Controller
public class VersionController {

    private UcdComponentService ucdComponentService;

    public VersionController(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    /**
     * Load the new version page.
     * 
     * @param model
     * @return
     */
    @GetMapping("/component/{componentname}/version/new")
    public String newVersion(@PathVariable String componentname, Model model) {
        UcdComponent component = ucdComponentService.findByName(componentname);
        Version version = new Version();
        version.setUcdComponent(component);
        model.addAttribute("version", version);
        return "versionform";

    }

    /**
     * Create a new version
     * 
     * @param project
     * @param model
     * @return
     */
    @PostMapping("/component/{componentname}/version")
    public String createVersion(Version version, Model model) {

        System.out.println("directory" + version.getDirectory());
        System.out.println(version.getUcdComponent().getName());
        System.out.println("----");

        ucdComponentService.saveVersion(version);
        System.out.println();

        return "redirect:/appcomponent/" + version.getUcdComponent().getName();

    }

    /**
     * Get Version by Name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/component/{componentname}/version/{versionname}")
    public String getVersionByName(@PathVariable String componentname, @PathVariable String versionname, Model model) {
        UcdComponent component = ucdComponentService.findByName(componentname);

        Version version = ucdComponentService.findVersionByName(component, versionname);

        if (version == null) {
            version = new Version();

            version.setUcdComponent(component);
        }
        model.addAttribute("version", version);
        return "version";
    }

    /**
     * Load the edit version page for the version with the specified name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/component/{componentname}/version/edit/{versionname}")
    public String editVersion(@PathVariable String componentname, @PathVariable String versionname, Model model) {

        UcdComponent component = ucdComponentService.findByName(componentname);

        Version version = ucdComponentService.findVersionByName(component, versionname);

        if (version == null) {
            version = new Version();

            version.setUcdComponent(component);
        }
        model.addAttribute("version", version);
        return "versionform";

    }

    /**
     * Update a version.
     * 
     * @param version
     * @return
     */
    @PostMapping("/component/{componentname}/version/{versionname}")

    public String updateVersion(Version version) {
        ucdComponentService.saveVersion(version);
        return "redirect:/component/" + version.getUcdComponent().getName() + "/version/";
    }

    /**
     * Delete a Version.
     * 
     * @param version
     * @return
     */
    @PostMapping("/component/{componentname}/version/delete/{version}")
    public String deleteVersion(Version version) {
        ucdComponentService.deleteVersion(version);
        return "redirect:/appcomponent/" + version.getUcdComponent().getName();
    }

}
