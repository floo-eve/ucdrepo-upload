package ch.fad.ucd.ucdrepoupload.services;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import ch.fad.ucd.ucdrepoupload.model.UcdComponent;
import ch.fad.ucd.ucdrepoupload.model.Version;

/**
 * UcdComponentService
 */
public interface UcdComponentService {

    public Set<UcdComponent> findAllByType(String type);

    public Set<UcdComponent> findAll();

    public UcdComponent save(UcdComponent component);

    public void delete(UcdComponent object);

    public void deleteVersion(Version version);

    // public void deleteById(ID id);

    public UcdComponent findByName(String name);

    public Version findVersionByName(UcdComponent component, String versionname);

    public Version saveVersion(Version version);

    public Version saveVersion(Version version, MultipartFile file);

    public String findComponentTypeDirectory(String type);

}