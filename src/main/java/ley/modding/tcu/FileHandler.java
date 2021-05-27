package ley.modding.tcu;

import ley.modding.tcu.model.VersionDiff;
import net.lingala.zip4j.ZipFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public void processDiff(VersionDiff diff) {

        for (VersionDiff.RemoveFile rem : diff.remove) {
            File f = getFile(rem.dir, rem.filename);
            if (f.exists()) {
                boolean del = f.delete();
                if (!del)
                    throw new RuntimeException("File deletion error");
            }
        }

        for (VersionDiff.AddFile add : diff.add) {
            try {
                downloadFile(add.dir, add.filename, new URL(add.url));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void processOverrides(URL overrides) {
        try {
            downloadFile(".", "temp.zip", overrides);
            File tmp = getFile(".", "temp.zip");
            ZipFile zip = new ZipFile(tmp);
            zip.extractAll(gamedir.getPath());
            tmp.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
