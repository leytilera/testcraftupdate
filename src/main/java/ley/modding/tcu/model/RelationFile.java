package ley.modding.tcu.model;

import ley.modding.tcu.Util;

import java.util.Objects;

public class RelationFile {

    public String id;
    public String dir;
    private String filename = null;
    public String url;

    public String filename() {
        if (filename == null) {
            String url = Util.resolve(this.url);
            String[] parts = url.split("/");
            filename = parts[parts.length - 1];
        }
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationFile that = (RelationFile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(dir, that.dir) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dir, filename, url);
    }
}
