package ch.set.ucd.ucd4u.services.map;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ch.set.ucd.ucd4u.model.Version;
import ch.set.ucd.ucd4u.model.UcdComponent;

/**
 * Map implementation for UcdComponentService
 */
@Profile("map")
@Service
public class UcdComponentMapServiceImpl { // implements UcdComponentService {

    protected Map<String, UcdComponent> map = new HashMap<>();

    public Set<UcdComponent> findAll() {
        return new HashSet<>(map.values());
    }

    public Set<UcdComponent> findAllByType(String type) {

        // Map -> Stream -> Filter -> Map
        Map<String, UcdComponent> collect = map.entrySet().stream().filter(map -> map.getValue().getType().equals(type))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return new HashSet<>(collect.values());
    }

    public UcdComponent findById(String id) {
        return findByName(id);

    }

    public Version findVersionByName(UcdComponent component, String versionname) {

        List<Version> versions = component.getVersions();
        for (Version version : versions) {
            if (version.getDirectory().equals(versionname)) {
                return version;
            }
        }
        return null; // --> @todo is this correct?
    }

    public String findComponentTypeDirectory(String type) {
        // TODO setdirectory
        return type;
    }

    public UcdComponent save(UcdComponent object) {

        if (object != null) {
            if (object.getName() == null) {
                // object.setId(getNextId());
                throw new RuntimeException("Object Name annot be null");
            }

            map.put(object.getName(), object);
        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return object;
    }

    public Version saveVersion(Version version) {
        if (version != null) {
            if (version.getDirectory() == null) {
                // object.setId(getNextId());
                throw new RuntimeException("Version Name cannot be null");
            }

            UcdComponent component = map.get(version.getUcdComponent().getName());
            component.addVersion(version);

        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return version;

    }

    // @Override
    // public void deleteById(String id) {
    // map.remove(id);
    // }

    public void delete(UcdComponent object) {
        map.entrySet().removeIf(entry -> entry.getValue().equals(object));
    }

    /**
     * 
     */
    public void deleteVersion(Version version) {

        UcdComponent component = version.getUcdComponent();
        component.removeVersion(version);

    }

    // private Long getNextId(){

    // Long nextId = null;

    // try {
    // nextId = Collections.max(map.keySet()) + 1;
    // } catch (NoSuchElementException e) {
    // nextId = 1L;
    // }

    // return nextId;
    // }

    public UcdComponent findByName(String name) {
        return map.get(name);
    }
}
