package ch.fad.ucd.ucdrepoupload.model;

import java.util.ArrayList;

public class UcdComponent {

    private String name;
    private String directory;
    private ArrayList<Version> versions;

    private ComponentType componentype;

    public UcdComponent() {

    }

    public UcdComponent(String name, String directory, ComponentType componenttype) {
        this.name = name;
        this.directory = directory;
        this.componentype = componenttype;
        this.versions = new ArrayList<Version>();
    }

    public void addVersion(Version version) {
        version.setUcdComponent(this);
        versions.add(version);
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

    public ComponentType getComponentype() {
        return this.componentype;
    }

    public void setComponentype(ComponentType componentype) {
        this.componentype = componentype;
    }

}