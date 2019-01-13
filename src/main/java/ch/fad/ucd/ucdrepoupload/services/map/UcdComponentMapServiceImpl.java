package ch.fad.ucd.ucdrepoupload.services.map;

import java.util.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;

/**
 * Map implementation for UcdComponentService
 */
@Profile("map")
@Service
public class UcdComponentMapServiceImpl implements UcdComponentService {

    protected Map<String, UcdComponent> map = new HashMap<>();

    public Set<UcdComponent> findAll() {
        return new HashSet<>(map.values());
    }

    public UcdComponent findById(String id) {
        return findByName(id);

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

    @Override
    public void deleteById(String id) {
        map.remove(id);
    }

    @Override
    public void delete(UcdComponent object) {
        map.entrySet().removeIf(entry -> entry.getValue().equals(object));
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

    @Override
    public UcdComponent findByName(String name) {
        return map.get(name);
    }
}
