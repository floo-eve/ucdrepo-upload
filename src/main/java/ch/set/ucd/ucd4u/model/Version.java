package ch.set.ucd.ucd4u.model;

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
public class Version implements Comparable<Version> {

    private String directory;
    private List<File> files;
    private UcdComponent ucdComponent;

    public Version() {
        log.debug("create version with default method");
        this.files = new ArrayList<File>();
    }

    public Version(String directory) {
        log.debug("create version with directory " + directory);
        this.directory = directory;
        this.files = new ArrayList<File>();
    }

    public Version(String directory, List<File> files) {
        this.directory = directory;
        this.files = files;
    }

    public Version(String directory, UcdComponent ucdComponent) {
        log.debug("create version with dir " + directory + " and component params");
        this.directory = directory;
        this.ucdComponent = ucdComponent;
        this.files = new ArrayList<File>();
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

    public String getAbsoluteVersionPath() {
        if (this.ucdComponent != null) {
            return ucdComponent.getDirectory() + "/" + this.directory;
        }

        return this.directory;
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

    @Override
    public int compareTo(Version that) {
        if (that == null)
            return 1;
        String[] thisParts = this.getDirectory().split("\\.");
        String[] thatParts = that.getDirectory().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

}