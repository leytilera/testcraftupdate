package ley.modding.tcu.model;

import java.util.List;

public class VersionDiff {

    public List<RemoveFile> remove;
    public List<AddFile> add;

    public static class RemoveFile {

        public String filename;
        public String dir;

    }

    public static class AddFile {

        public String url;
        public String filename;
        public String dir;

    }

}
