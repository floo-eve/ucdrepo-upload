package ch.set.ucd.ucd4u.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.set.ucd.ucd4u.model.SearchResult;
import ch.set.ucd.ucd4u.model.UcdComponent;
import ch.set.ucd.ucd4u.services.UcdComponentService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/component")
public class ComponentSearchRestController {
    
    private UcdComponentService ucdComponentService;

    public ComponentSearchRestController(UcdComponentService ucdComponentService) {
        this.ucdComponentService = ucdComponentService;
    }

    /**
     * Search all components with certain name in all homeBases
     * 
     * @param name searchString for the name of the component
     * 
     * @return
     */
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<SearchResult> searchComponentsByName(@RequestParam String searchValue) {
        List<UcdComponent> list = new ArrayList<UcdComponent>(
                ucdComponentService.searchByName(searchValue));
        list.sort(Comparator.comparing(UcdComponent::getName));

        List<SearchResult> components = new ArrayList<>();


        for (UcdComponent ucdComponent : list) {
            components.add(new SearchResult(ucdComponent.getName(), ucdComponent.getHomeBase() +"/type/"+ucdComponent.getType()+"/component/"+ucdComponent.getName()));
        }

      
        log.debug("Search list");
        return components;

    }

}
