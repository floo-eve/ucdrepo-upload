package ch.set.ucd.ucd4u.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UcdComponent {

    private String name;
    private String directory;
    private ArrayList<Version> versions;
    private String parentDirectory;
    private String type;

    public UcdComponent() {

    }

    public UcdComponent(String name, String parentDirectory, String type) {
        this.name = name;
        this.parentDirectory = parentDirectory;
        this.type = type;
        this.versions = new ArrayList<Version>();
        this.directory = this.parentDirectory + "/" + name;
    }

    public void addVersion(Version version) {
        version.setUcdComponent(this);
        versions.add(version);
    }

    public void removeVersion(Version version) {
        versions.remove(version);
    }

}