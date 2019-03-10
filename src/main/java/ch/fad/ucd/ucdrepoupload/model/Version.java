package ch.fad.ucd.ucdrepoupload.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Version
 */
@Getter
@Setter
@Slf4j
public class Version {

    private String directory;
    private List<File> files;
    private UcdComponent ucdComponent;

    public Version() {
        log.debug("create version with default method");
        this.files = new ArrayList<File>();
    }

    public Version(String directory) {
        log.debug("create version with directory");
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