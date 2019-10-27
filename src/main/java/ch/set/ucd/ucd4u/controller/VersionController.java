package ch.set.ucd.ucd4u.controller;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ch.set.ucd.ucd4u.exception.ComponentExistsException;
import ch.set.ucd.ucd4u.model.UcdComponent;
import ch.set.ucd.ucd4u.model.Version;
import ch.set.ucd.ucd4u.services.UcdComponentService;
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

        ucdComponentService.saveVersion(version);
        ucdComponentService.addFileToVersion(version, file);

        return "redirect:/component/" + type + "/" + version.getUcdComponent().getName();

    }

    /**
     * Create a a new file in the version
     * 
     * @param versionname
     * @param model
     * @return
     */
    @PostMapping("/component/{type}/{componentname}/version/{versionname}/file")
    public String addFileToVersion(@PathVariable String type, @RequestParam("file") MultipartFile file,
            @PathVariable String componentname, @PathVariable String versionname,
            @RequestParam("absoluteParentPath") String absoluteParentPath, Model model) {

        UcdComponent component = ucdComponentService.findByName(componentname);

        Version version = ucdComponentService.findVersionByName(component, versionname);

        log.debug("new file in directory: " + version.getDirectory() + absoluteParentPath);
        log.debug(version.getUcdComponent().getName());
        log.debug("file to upload: " + file.getOriginalFilename());
        log.debug("----");

        ucdComponentService.addFileToVersion(version, absoluteParentPath, file);

        return "redirect:/component/" + type + "/" + version.getUcdComponent().getName() + "/version/edit/"
                + version.getDirectory();

    }

    /**
     * Create a a new directory in the version
     * 
     * @param version
     * @param model
     * @return
     */
    @PostMapping("/component/{type}/{componentname}/version/{versionname}/dir")
    public String addDirToVersion(@PathVariable String type, @RequestParam("dirname") String dirname,
            @PathVariable String componentname, @PathVariable String versionname,
            @RequestParam("absoluteParentPath") String absoluteParentPath, Model model) {

        log.debug("-------------------------------------------------------");
        UcdComponent component = ucdComponentService.findByName(componentname);

        Version version = ucdComponentService.findVersionByName(component, versionname);

        log.debug("absoluteParentPath: " + absoluteParentPath);
        log.debug(version.getUcdComponent().getName());
        log.debug("----");

        ucdComponentService.addDirToVersion(version, absoluteParentPath + File.separator + dirname);

        return "redirect:/component/" + type + "/" + version.getUcdComponent().getName() + "/version/edit/"
                + version.getDirectory();

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

        return "versionformedit";

    }

    /**
     * Change the version name.
     * 
     * @param version
     * @return
     */
    @PostMapping("/component/{type}/{componentname}/version/rename/{versionname}")
    public String changeVersionName(@PathVariable String type, @PathVariable String componentname,
            @PathVariable String versionname, @RequestParam("directory") String newName, Model model) {
        log.debug("**** rename to " + newName);

        UcdComponent component = ucdComponentService.findByName(componentname);
        Version version = ucdComponentService.findVersionByName(component, versionname);

        try {

            version = ucdComponentService.changeVersionName(newName, version);

        } catch (ComponentExistsException e) {

            log.debug("error in renaming");
            model.addAttribute("error", "Version name " + version.getDirectory() + " already exists");

        }

        // reload version, with new file structure
        version = ucdComponentService.findVersionByName(component, newName);
        model.addAttribute("version", version);

        return "versionformedit";

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
