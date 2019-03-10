package ch.fad.ucd.ucdrepoupload.services.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ch.fad.ucd.ucdrepoupload.StorageException;
import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;
import ch.fad.ucd.ucdrepoupload.services.UcdComponentService;
import lombok.extern.slf4j.Slf4j;

/**
 * File based Repo for UcdComponentService
 */
@Profile("file")
@Primary
@Service
@Slf4j
public class UcdComponentFileServiceImpl implements UcdComponentService {

    protected Map<String, UcdComponent> map = new HashMap<>();
    @Value("${file.upload.dir}")
    public String repoDir = "/home/floo/ucdrepo";

    private Path rootLocation;

    public UcdComponentFileServiceImpl() {

        this.rootLocation = Paths.get(repoDir);
        log.debug("Root Location is: " + repoDir);

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
                    log.debug("directory: " + file.getCanonicalPath());
                    for (File dirComponent : file.listFiles()) {
                        if (dirComponent.isDirectory()) {

                            UcdComponent ucdcomponent = new UcdComponent(dirComponent.getName(),
                                    dirComponent.getParent(), dirComponent.getParentFile().getName());

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

    public Set<UcdComponent> findAllByType(String type) {

        // Map -> Stream -> Filter -> Map
        Map<String, UcdComponent> collect = map.entrySet().stream().filter(map -> map.getValue().getType().equals(type))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return new HashSet<>(collect.values());
    }

    public String findComponentTypeDirectory(String type) {
        return repoDir + "/" + type;
    }

    public Version findVersionByName(UcdComponent component, String versionname) {

        List<Version> versions = component.getVersions();
        for (Version version : versions) {
            if (version.getDirectory().equals(versionname)) {
                log.debug("find all Files from " + version.getDirectory());

                if (version.getFiles().size() == 0) {
                    // Try to load Files
                    // Find the absolute path of the version and load Files
                    String path = version.getUcdComponent().getDirectory() + "/" + version.getDirectory();
                    log.debug(version.getUcdComponent().getDirectory());
                    File versiondir = new File(path);

                    addAllFilesToVersion(versiondir, version);
                    // File[] files = f.listFiles();

                    // if (files != null) {
                    // for (File file : files) {
                    // // if (file.isDirectory()) --> get all child files
                    // version.addFile(file);
                    // }
                    // }

                }
                return version;
            }
        }
        return null; // --> @todo is this correct?
    }

    public static void addAllFilesToVersion(File dir, Version version) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    log.debug("directory:" + file.getCanonicalPath());
                    addAllFilesToVersion(file, version);
                } else {
                    log.debug("     file:" + file.getCanonicalPath());
                    version.addFile(file);
                }
            }
        } catch (IOException e) {
            log.error("error in traversing files", e);
        }
    }

    public UcdComponent findById(String id) {
        return findByName(id);

    }

    public UcdComponent save(UcdComponent component) {

        if (component != null) {
            if (component.getName() == null) {
                throw new RuntimeException("Object Name annot be null");
            }

            component.setParentDirectory(repoDir + "/" + component.getType());
            component.setDirectory(component.getParentDirectory() + "/" + component.getName());

            // save to filesystem
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
            // save to filesystem
            log.debug("save version: " + version.getDirectory());
            new File(component.getDirectory() + '/' + version.getDirectory()).mkdirs();

        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return version;

    }

    public Version saveVersion(Version version, MultipartFile file) {
        saveVersion(version);

        return version;
    }

    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
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
