package ch.set.ucd.ucd4u.services.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ch.set.ucd.ucd4u.StorageException;
import ch.set.ucd.ucd4u.exception.ComponentExistsException;
import ch.set.ucd.ucd4u.model.UcdComponent;
import ch.set.ucd.ucd4u.model.Version;
import ch.set.ucd.ucd4u.services.UcdComponentService;
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
    private String repoDir; // = "/home/floo/ucdrepo";

    private Path rootLocation;

    @Autowired
    public UcdComponentFileServiceImpl(@Value("${file.upload.dir}") final String repodir) {

        this.repoDir = repodir;
        this.rootLocation = Paths.get(this.repoDir);

        log.debug("Root Location is: " + repoDir);

        initialize();
    }

    private void initialize() {
        System.out.println("initialize File Tree with all components in mw and application");
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

                            // for (File version : dirComponent.listFiles()) {
                            // if (version.isDirectory()) {
                            // File[] filesFromVersion = version.listFiles();
                            // Version dirVersion = new Version(version.getName());
                            // ucdcomponent.addVersion(dirVersion);
                            // }
                            // }

                            // // Sort files by Versionnumbers
                            // List<Versions> files = ucdcomponent.getVersions();
                            // files.sort(Comparator.comparing(File::getName));

                            // for (File file : files) {
                            // log.debug(file.getName());

                            // }

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
                    version.addFile(file);
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
                throw new RuntimeException("Object Name cannot be null");
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

    /**
     * Rename the component and move directory structure
     * 
     * @param oldName   old directory name
     * @param component UcdComponent with the new directory
     */
    public UcdComponent rename(String oldName, UcdComponent component) throws ComponentExistsException {
        File dirOld = new File(component.getParentDirectory() + "/" + oldName);
        File dirNew = new File(component.getDirectory());
        log.debug("rename component " + oldName + "  to " + component.getName());
        try {
            FileUtils.moveDirectory(dirOld, dirNew);
            map.remove(oldName);
            map.put(component.getName(), component);

        } catch (FileExistsException e) {
            log.error("Renaming failed, because directory already exists", e);
            throw new ComponentExistsException("Directory " + component.getDirectory());
        } catch (IOException e) {
            log.error("Rename Component not successful");
            log.error("Error rename component " + oldName, e);

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

        if (file != null && !file.isEmpty()) {
            storeFiletoVersion(version, file);
        }

        return version;
    }

    public void storeFiletoVersion(Version version, MultipartFile file) {
        log.debug("storefiletoVersion");
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
                log.debug("Copy 1 file to " + version.getDirectory());
                // Files.copy(inputStream,
                // this.rootLocation.resolve(version.getDirectory()),StandardCopyOption.REPLACE_EXISTING);
                Path path = Paths.get(version.getAbsoluteVersionPath(), file.getOriginalFilename());
                log.debug("copy 1 file to " + path.toString());
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    // @Override
    // public void deleteById(String id) {
    // map.remove(id);
    // }

    public void delete(UcdComponent component) {
        map.entrySet().removeIf(entry -> entry.getValue().equals(component));

        deleteFileDirectory(component.getDirectory());
    }

    /**
     * Delete a version from a component
     */
    public void deleteVersion(Version version) {

        UcdComponent component = version.getUcdComponent();
        component.removeVersion(version);

        log.debug("delete Version " + version.getAbsoluteVersionPath());
        // Delete Version from Filesystem
        deleteFileDirectory(version.getAbsoluteVersionPath());
    }

    /**
     * Find a component by name
     * 
     * @param name String
     */
    public UcdComponent findByName(String name) {
        UcdComponent component = map.get(name);

        Path path = Paths.get(component.getParentDirectory(), component.getName());
        File dirComponent = path.toFile();

        ArrayList<Version> versions = new ArrayList<Version>();

        for (File fileVersion : dirComponent.listFiles()) {
            if (fileVersion.isDirectory()) {

                Version version = new Version(fileVersion.getName(), component);
                versions.add(version);
            }
        }

        // versions.sort(Collections.reverseOrder(Comparator.comparing(Version::getDirectory)));
        versions.sort(Collections.reverseOrder(null));

        component.setVersions(versions);
        return component;
    }

    public void deleteFileDirectory(String directory) {

        log.debug("delete path " + directory);
        try {
            FileUtils.deleteDirectory(new File(directory));
        } catch (IOException e) {
            log.error("Deletion wasn't successful");
            log.error("Errormessage: ", e);

        }
    }
}
