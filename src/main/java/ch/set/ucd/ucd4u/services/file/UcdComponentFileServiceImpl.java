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

    private Map<String, HashMap<String, UcdComponent>> repoMaps = new HashMap<>();
    private String repoDir; // = "/home/floo/ucdrepo";
    private Map<String, String> repoDirAbsolutename = new HashMap<>();
    private Map<String, ArrayList<String>> repoTypes = new HashMap<>();

    // private Path rootLocation;

    @Autowired
    public UcdComponentFileServiceImpl(@Value("${file.home.dir}") final String repodir) {

        this.repoDir = repodir;
        // this.rootLocation = Paths.get(this.repoDir);

        System.out.println("initialize File Tree with all components " + repoDir);

        initialize();
    }

    private void initialize() {
        try {
            String[] repos = repoDir.split(" ");

            for (String repo : repos) {
                File repoFile = new File(repo); // current directory

                if (!repoFile.isDirectory()) {
                    log.error("Home Base is not a directory");
                    break;
                } else {
                    repoDirAbsolutename.put(repoFile.getName(), repo);
                    log.info("initialize " + repoFile.getName());
                }

                String homeBase = repoFile.getName();

                // Make a new HashMap for a repo and put it to repoMaps
                HashMap<String, UcdComponent> map = new HashMap<>();
                repoMaps.put(homeBase, map);

                // make a list for all types and put it to repoTypes Map
                ArrayList<String> types = new ArrayList<String>();
                repoTypes.put(homeBase, types);

                File[] files = repoFile.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) { // this is the types directory
                        log.debug("type directory: " + file.getCanonicalPath());
                        // add types to the typeslist
                        types.add(file.getName());

                        for (File dirComponent : file.listFiles()) {
                            if (dirComponent.isDirectory()) {

                                UcdComponent ucdcomponent = new UcdComponent(dirComponent.getName(), homeBase,
                                        dirComponent.getParent(), dirComponent.getParentFile().getName());

                                map.put(ucdcomponent.getName(), ucdcomponent);

                            }
                        }

                    } else {
                        System.out.print("unexpected file:" + file.getCanonicalFile());

                    }
                }

                Collections.sort(types);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    /**
     * 
     */
    public Set<UcdComponent> findAllComponents(String homeBase) {
        Map<String, UcdComponent> map = this.repoMaps.get(homeBase);

        return new HashSet<>(map.values());
    }

    /**
     * 
     */
    public List<String> findAllTypes(String homeBase) {
        ArrayList<String> types = this.repoTypes.get(homeBase);

        return types;
    }

    public Set<UcdComponent> findAllComponentsByType(String homeBase, String type) {

        Map<String, UcdComponent> map = this.repoMaps.get(homeBase);

        // Map -> Stream -> Filter -> Map
        Map<String, UcdComponent> collect = map.entrySet().stream()
                .filter(mapt -> mapt.getValue().getType().equals(type))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return new HashSet<>(collect.values());
    }

    public String findComponentTypeDirectory(String baseHome, String type) {
        return repoDirAbsolutename.get(baseHome) + "/" + type;
    }

    public Version findVersionByName(UcdComponent component, String versionname) {

        List<Version> versions = component.getVersions();
        for (Version version : versions) {
            // Only load the files of the wanted version
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
        return null; // --> shouldn't be null
    }

    private static void addAllFilesToVersion(File dir, Version version) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    log.debug("directory:" + file.getCanonicalPath());
                    version.addFile(file);
                    addAllFilesToVersion(file, version);
                } else {
                    log.debug(" file:" + file.getCanonicalPath());
                    version.addFile(file);
                }
            }
        } catch (IOException e) {
            log.error("error in traversing files", e);
        }
    }

    // public UcdComponent findById(String id) {
    // return findByName(id);

    // }

    public UcdComponent save(UcdComponent component) {

        if (component != null) {
            if (component.getName() == null) {
                throw new RuntimeException("Object Name cannot be null");
            }

            log.debug("HomeBase for to to be saved component: " + repoDirAbsolutename.get(component.getHomeBase()));
            component.setParentDirectory(repoDirAbsolutename.get(component.getHomeBase()) + "/" + component.getType());
            component.setDirectory(component.getParentDirectory() + "/" + component.getName());

            // save to filesystem
            new File(component.getDirectory()).mkdirs();
            repoMaps.get(component.getHomeBase()).put(component.getName(), component);
        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return component;
    }

    /**
     * 
     * @param newName new version name
     * @param version existing version
     * @throws ComponentExistsException
     */
    public Version changeVersionName(String newName, Version version) throws ComponentExistsException {
        // File dirOld = new File()
        log.debug("change Name from " + version.getDirectory() + " to " + newName);

        File dirNew = new File(version.getUcdComponent().getDirectory() + File.separator + newName);
        File dirOld = new File(version.getAbsoluteVersionPath());
        try {
            FileUtils.moveDirectory(dirOld, dirNew);
            version.setDirectory(newName);
            version.setFiles(new ArrayList<File>());

        } catch (FileExistsException e) {
            log.error("Renaming failed, because version directory already exists", e);
            throw new ComponentExistsException("Directory " + newName);
        } catch (IOException e) {
            log.error("Rename version not successful");
            log.error("Error rename version to " + newName, e);

        }
        return version;
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
            repoMaps.get(component.getHomeBase()).remove(oldName);
            repoMaps.get(component.getHomeBase()).put(component.getName(), component);

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

            UcdComponent component = repoMaps.get(version.getUcdComponent().getHomeBase())
                    .get(version.getUcdComponent().getName());
            component.addVersion(version);
            // save to filesystem
            log.debug("save version: " + version.getDirectory());
            new File(component.getDirectory() + '/' + version.getDirectory()).mkdirs();

        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return version;

    }

    public Version addFileToVersion(Version version, MultipartFile file) {
        // saveVersion(version);

        if (file != null && !file.isEmpty()) {
            storeFiletoVersion(version, file);
        }

        return version;
    }

    /**
     * Adds a subdirectory to a version directory
     * 
     * @param version      Version where dir is to be added
     * @param absolutePath Path to subdirectory that will be created
     */
    public Version addDirToVersion(Version version, String absolutePath) {
        // todo save a new directory to a version
        File dirNew = new File(absolutePath);
        dirNew.mkdirs();
        version.addFile(dirNew);
        return version;
    }

    /**
     * Adds a File to a subdirectory of a version
     * 
     * @param version  Version where file is to be added
     * @param filepath Path to subdirectory where the file will be added
     */
    public Version addFileToVersion(Version version, String filePath, MultipartFile file) {
        // saveVersion(version);

        if (file != null && !file.isEmpty() && filePath != "") {
            storeFiletoVersion(version, filePath, file);
        }

        return version;
    }

    /**
     * Store a file to the root directory of the version
     * 
     * @param version
     * @param file
     */
    private void storeFiletoVersion(Version version, MultipartFile file) {
        storeFiletoVersion(version, version.getAbsoluteVersionPath(), file);
    }

    /**
     * Store a file to an absolute Path in the version directory
     * 
     * @param version
     * @param absoluteFilePath
     * @param file
     */
    private void storeFiletoVersion(Version version, String absoluteFilePath, MultipartFile file) {
        log.debug("storefiletoVersion to " + absoluteFilePath);
        log.debug("filename org: " + file.getOriginalFilename());

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        log.debug("filename after cleanPath: " + filename);

        if (filename.contains("/")) {
            // Windows path information have to be extracted
            String[] subpaths = filename.split("/");
            filename = subpaths[subpaths.length - 1];
            log.debug("filename extraced: " + filename);

        }

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (absoluteFilePath.contains("..") || filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + filename);
            }
            if (!absoluteFilePath.startsWith(version.getAbsoluteVersionPath())) {
                // If the new file is not within the version path abort
                throw new StorageException("path is not within the version path, are you naughty?");
            }
            try (InputStream inputStream = file.getInputStream()) {
                log.debug("Copy 1 file to " + absoluteFilePath);
                // Files.copy(inputStream,
                // this.rootLocation.resolve(version.getDirectory()),StandardCopyOption.REPLACE_EXISTING);
                Path path = Paths.get(absoluteFilePath, filename);
                log.debug("copy 1 file to " + path.toString());
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    /**
     * @param component component that will be deleted
     */
    public void delete(UcdComponent component) {
        repoMaps.get(component.getHomeBase()).entrySet().removeIf(entry -> entry.getValue().equals(component));

        try {
            deleteFileDirectory(component.getDirectory());
        } catch (IOException e) {
            log.error("Error in deleting component " + component.getDirectory());
        }

    }

    /**
     * Delete a version from a component
     * 
     * @param version
     */
    public void deleteVersion(Version version) {

        UcdComponent component = version.getUcdComponent();

        log.debug("delete Version " + version.getAbsoluteVersionPath());
        // Delete Version from Filesystem
        try {
            deleteFileDirectory(version.getAbsoluteVersionPath());
            component.removeVersion(version);

        } catch (IOException e) {
            log.error("Error in deleten Version " + version.getAbsoluteVersionPath());
        }
    }

    /**
     * Delete a file or a directory from the version
     * 
     * @param version
     * @param absoluteFilePath
     */
    public void deleteFile(Version version, String absoluteFilePath) {
        log.debug("delete File " + absoluteFilePath);

        if (!absoluteFilePath.contains("..")) {
            try {
                deleteFileDirectory(absoluteFilePath);
                version.removeFile(new File(absoluteFilePath));
            } catch (IOException e) {
                log.error("Error in deleting file in version");
            }
        }
    }

    /**
     * Find a component by name
     * 
     * @param homeBase String
     * @param name     String
     */
    public UcdComponent findByName(String homeBase, String name) {
        UcdComponent component = repoMaps.get(homeBase).get(name);

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

    private void deleteFileDirectory(String directory) throws IOException {

        if (directory != File.separator) {
            // only delete when it is not the root Directory

            log.debug("delete path " + directory);

            File todelete = new File(directory);
            if (todelete.isDirectory()) {
                FileUtils.deleteDirectory(new File(directory));

            } else {
                FileUtils.deleteQuietly(todelete);
            }
        }
    }
}
