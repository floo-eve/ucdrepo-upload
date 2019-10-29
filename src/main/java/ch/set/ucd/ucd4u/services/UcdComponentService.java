package ch.set.ucd.ucd4u.services;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import ch.set.ucd.ucd4u.exception.ComponentExistsException;
import ch.set.ucd.ucd4u.model.UcdComponent;
import ch.set.ucd.ucd4u.model.Version;

/**
 * UcdComponentService
 */
public interface UcdComponentService {

    public Set<UcdComponent> findAllByType(String type);

    public Set<UcdComponent> findAll();

    public UcdComponent save(UcdComponent component);

    public UcdComponent rename(String oldName, UcdComponent component) throws ComponentExistsException;

    public void delete(UcdComponent object);

    public void deleteVersion(Version version);

    public void deleteFile(Version version, String absoluteFilePath);

    // public void deleteById(ID id);

    public UcdComponent findByName(String name);

    public Version findVersionByName(UcdComponent component, String versionname);

    public Version saveVersion(Version version);

    public Version changeVersionName(String oldName, Version version) throws ComponentExistsException;

    public Version addFileToVersion(Version version, MultipartFile file);

    public Version addFileToVersion(Version version, String filePath, MultipartFile file);

    public Version addDirToVersion(Version version, String dirPath);

    public String findComponentTypeDirectory(String type);

}