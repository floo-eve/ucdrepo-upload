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
                                    dirComponent.getParent(), new ComponentType(dirComponent.getParent()));

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

    public String findComponentTypeDirectory(ComponentType type) {
        return repoDir + "/" + type.getType();
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

    public UcdComponent findById(String id) {
        return findByName(id);

    }

    public UcdComponent save(UcdComponent component) {

        if (component != null) {
            if (component.getName() == null) {
                throw new RuntimeException("Object Name annot be null");
            }

            component.setParentDirectory(repoDir + "/" + component.getComponenttype().getType());
            component.setDirectory(component.getParentDirectory() + "/" + component.getName());

            // TODO save to filesystem
            new File(component.getDirectory()).mkdirs();
            map.put(component.getName(), component);
        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return component;
    }

    public Version saveVersion(Version version) {
        if (version != null) {
            if (version.getDirectory() == null) {
                // object.setId(getNextId());
                throw new RuntimeException("Version Name cannot be null");
            }

            UcdComponent component = map.get(version.getUcdComponent().getName());
            component.addVersion(version);
            // TODO save to filesystem

        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return version;

    }

    // @Override
    // public void deleteById(String id) {
    // map.remove(id);
    // }

    @Override
    public void delete(UcdComponent object) {
        map.entrySet().removeIf(entry -> entry.getValue().equals(object));
    }

    @Override
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

    @Override
    public UcdComponent findByName(String name) {
        return map.get(name);
    }
}
