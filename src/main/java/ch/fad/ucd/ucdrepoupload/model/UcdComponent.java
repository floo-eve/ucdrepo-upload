package ch.fad.ucd.ucdrepoupload.model;

import java.util.ArrayList;

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

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the directory
     */
    public String getDirectory() {
        return this.directory;
    }

    public String getParentDirectory() {
        return this.parentDirectory;
    }

    public void setParentDirectory(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ArrayList<Version> getVersions() {
        return this.versions;
    }

    public void setVersions(ArrayList<Version> versions) {
        this.versions = versions;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}