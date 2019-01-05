package ch.fad.ucd.ucdrepoupload.model;

import java.util.Objects;

/**
 * Version
 */
public class Version {

    private String directory;
    private String file;
    private UcdComponent ucdComponent;

    public Version() {
    }

    public Version(String directory, String file) {
        this.directory = directory;
        this.file = file;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public Version file(String file) {
        this.file = file;
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
        return Objects.equals(directory, version.directory) && Objects.equals(file, version.file)
                && Objects.equals(ucdComponent, version.ucdComponent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory, file, ucdComponent);
    }

    @Override
    public String toString() {
        return "{" + " directory='" + getDirectory() + "'" + ", file='" + getFile() + "'" + ", ucdComponent='"
                + getUcdComponent() + "'" + "}";
    }

}