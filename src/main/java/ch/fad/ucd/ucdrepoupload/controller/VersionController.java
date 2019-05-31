package ch.fad.ucd.ucdrepoupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;
import lombok.extern.slf4j.Slf4j;

/**
 * VersionController
 */
@Controller
@Slf4j
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
    @GetMapping("/component/{type}/{componentname}/version/new")
    public String newVersion(@PathVariable String type, @PathVariable String componentname, Model model) {
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
    @PostMapping("/component/{type}/{componentname}/version")
    public String createVersion(@PathVariable String type, @RequestParam("file") MultipartFile file, Version version,
            Model model) {

        log.debug("new directory: " + version.getDirectory());
        log.debug(version.getUcdComponent().getName());
        log.debug("file to upload: " + file.getOriginalFilename());
        log.debug("----");

        // TODO check if version allready exists
        if (version.getDirectory().equals("")) {
            return "versionform";
        }

        ucdComponentService.saveVersion(version, file);

        return "redirect:/component/" + type + "/" + version.getUcdComponent().getName();

    }

    /**
     * Get Version by Name.
     * 
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/component/{type}/{componentname}/version/{versionname}")
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
    @GetMapping("/component/{type}/{componentname}/version/edit/{versionname}")
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
    @PostMapping("/component/{type}/{componentname}/version/{versionname}")

    public String updateVersion(@PathVariable String type, Version version) {
        ucdComponentService.saveVersion(version);
        return "redirect:/component/" + type + "/" + version.getUcdComponent().getName() + "/version/";
    }

    /**
     * Delete a Version.
     * 
     * @param version
     * @return
     */
    @GetMapping("/component/{type}/{componentname}/version/delete/{versionname}")
    public String deleteVersion(@PathVariable String type, @PathVariable String componentname,
            @PathVariable String versionname) {

        log.debug(componentname + ": delete Version " + versionname);
        UcdComponent component = ucdComponentService.findByName(componentname);

        Version version = ucdComponentService.findVersionByName(component, versionname);
        ucdComponentService.deleteVersion(version);
        return "redirect:/component/" + type + "/" + componentname;
    }

}
