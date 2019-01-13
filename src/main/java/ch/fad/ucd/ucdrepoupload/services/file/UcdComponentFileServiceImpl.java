package ch.fad.ucd.ucdrepoupload.services.file;

import java.io.File;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ch.fad.ucd.ucdrepoupload.model.ComponentType;
import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;

/**
 * File based Repo for UcdComponentService
 */
@Profile("file")
@Primary
@Service
public class UcdComponentFileServiceImpl implements UcdComponentService {

    protected Map<String, UcdComponent> map = new HashMap<>();
    @Value("${file.upload.dir}")
    public String repoDir = "/home/floo/ucdrepo";

    public UcdComponentFileServiceImpl() {

        initialize();
    }

    private void initialize() {
        System.out.println("initialize File Tree");
        try {
            System.out.println("File Directory: " + repoDir);
            File f = new File(repoDir); // current directory

            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.print("directory: " + file.getCanonicalPath());
                    for (File dirComponent : file.listFiles()) {
                        if (dirComponent.isDirectory()) {

                            UcdComponent ucdcomponent = new UcdComponent(dirComponent.getName(),
                                    dirComponent.getAbsolutePath(), new ComponentType(dirComponent.getParent()));

                            for (File version : dirComponent.listFiles()) {
                                if (version.isDirectory()) {
                                    // TODO Add Files to Version
                                    File[] filesFromVersion = version.listFiles();
                                    Version dirVersion = new Version(version.getName());
                                    ucdcomponent.addVersion(dirVersion);
                                }
                            }

                            map.put(ucdcomponent.getName(), ucdcomponent);

                        }
                    }

                } else {
                    System.out.print("unexpected file:" + file.getCanonicalFile());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

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
