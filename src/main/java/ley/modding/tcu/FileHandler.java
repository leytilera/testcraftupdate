package ley.modding.tcu;

import ley.anvil.addonscript.v1.AddonscriptJSON;
import ley.modding.tcu.model.Config;
import ley.modding.tcu.model.RelationFile;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FileHandler {

    File gamedir;

    public FileHandler(File gamedir) {
        this.gamedir = gamedir;
    }

    private File getFile(String dir, String filename) {
        return new File(gamedir.getAbsolutePath() + "/" + dir + "/" + filename);
    }

    private void downloadFile(String dir, String filename, URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        BufferedInputStream in = new BufferedInputStream(con.getInputStream());
        FileOutputStream out = new FileOutputStream(getFile(dir, filename));
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            out.write(dataBuffer, 0, bytesRead);
        }
        in.close();
        out.close();
    }

    public void removeFiles(List<RelationFile> rels) {
        for (RelationFile rel : rels) {
            File f = getFile(rel.dir, rel.filename());
            if (f.exists()) {
                if (!f.delete()) {
                    throw new RuntimeException("Updater is not able to delete File. Please delete it manually: " + f.getAbsolutePath());
                }
            }
        }
    }

    public void addFiles(List<RelationFile> rels) {
        for (RelationFile rel : rels) {
            try {
                System.out.println("Downloading " + rel.id);
                downloadFile(rel.dir, rel.filename(), new URL(rel.url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void processOverrides(String overrides, AddonscriptJSON as, Config config) {
        try {
            System.out.println("Downloading overrides...");
            downloadFile(".", "temp.zip", new URL(overrides));
            File tmp = getFile(".", "temp.zip");
            ZipFile zip = new ZipFile(tmp);
            for (AddonscriptJSON.File f : as.versions.get(0).files) {
                if (f.installer.equals("internal.override")) {
                    String loc = buildPath(config.repository, f.link);
                    List<FileHeader> headers = zip.getFileHeaders();
                    for (FileHeader header : headers) {
                        if (header.toString().startsWith(loc) && !header.isDirectory())
                            zip.extractFile(header, ".", header.toString().replace(loc, ""));
                    }
                }
            }
            tmp.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPath(String repo, String loc) {
        String fileloc = loc.replace("file://", "");
        if (fileloc.startsWith("..")) {
            fileloc = fileloc.replace("..", "");
        } else {
            fileloc = "/src/" + fileloc;
        }
        return repo + fileloc + "/";
    }

}
