package ch.fad.ucd.ucdrepoupload.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Version
 */
public class Version {

    private String directory;
    private List<File> files;
    private UcdComponent ucdComponent;

    public Version() {
    }

    public Version(String directory) {
        this.directory = directory;
        this.files = new ArrayList<File>();
    }

    public Version(String directory, List<File> files) {
        this.directory = directory;
        this.files = files;
    }

    public void addFile(File file) {
        this.files.add(file);
    }

    public boolean removeFile(File file) {
        return this.files.remove(file);
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public UcdComponent getUcdComponent() {
        return this.ucdComponent;
    }

    public void setUcdComponent(UcdComponent ucdComponent) {
        this.ucdComponent = ucdComponent;
    }

    public Version directory(String directory) {
        this.directory = directory;
        return this;
    }

    public Version file(List<File> files) {
        this.files = files;
        return this;
    }

    public Version ucdComponent(UcdComponent ucdComponent) {
        this.ucdComponent = ucdComponent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Version)) {
            return false;
        }
        Version version = (Version) o;
        return Objects.equals(directory, version.directory) && Objects.equals(files, version.files)
                && Objects.equals(ucdComponent, version.ucdComponent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory, ucdComponent);
    }

    @Override
    public String toString() {
        return "{" + " directory='" + getDirectory() + "'" + "'" + ", ucdComponent='" + getUcdComponent() + "'" + "}";
    }

}