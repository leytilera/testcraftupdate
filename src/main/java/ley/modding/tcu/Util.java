package ley.modding.tcu;

import ley.anvil.addonscript.v1.AddonscriptJSON;
import ley.modding.tcu.model.Config;
import ley.modding.tcu.model.RelationFile;
import ley.modding.tcu.model.Release;
import ley.modding.tcu.model.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static Config getConfig(File local) throws IOException {
        Reader r = new FileReader(local);
        Config cfg = Config.fromJSON(r);
        r.close();
        return cfg;
    }

    public static Version checkVersion(Config conf) {
        GiteaAPI api = conf.getAPI();
        List<Release> releases = api.getReleases();
        return new Version(releases.get(0).tag, conf.version, releases.get(0).zipUrl);
    }

    public static String resolve(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            con.setInstanceFollowRedirects(false);
            int status = con.getResponseCode();
            if (status == 301 || status == 302) {
                String loc = con.getHeaderField("location");
                return loc;
            } else {
                return url;
            }
        } catch (IOException e) {
            return url;
        }
    }

    public static List<RelationFile> getRelations(AddonscriptJSON as) {
        List<RelationFile> relations = new ArrayList<>();
        Map<String, String> repos = new HashMap<>();

        for (AddonscriptJSON.Repository r : as.repositories) {
            String url = r.url;
            if (!url.endsWith("/")) {
                url += "/";
            }
            repos.put(r.id, url);
        }

        for (AddonscriptJSON.Relation r : as.versions.get(0).relations) {
            if (r.type.equals("modloader"))
                continue;
            RelationFile rel = new RelationFile();
            rel.id = r.id;
            rel.dir = r.file.installer.split(":")[1];
            if (r.file.link != null && !r.file.link.isEmpty()) {
                rel.url = r.file.link;
            } else {
                String[] parts = r.file.artifact.split(":");
                if (parts[0].equals("curse.maven")) {
                    parts[1] = parts[1] + "-" + parts[1];
                }
                parts[0] = parts[0].replace('.', '/');
                rel.url = repos.get(r.file.repository) + parts[0] + "/" + parts[1] + "/" + parts[2] + "/" + parts[1] + "-" + parts[2];
                if (parts.length == 4) {
                    rel.url += "-";
                    rel.url += parts[3];
                }
                rel.url += ".jar";
            }

            relations.add(rel);
        }
        return relations;
    }

    public static List<RelationFile> getToRemove(List<RelationFile> old, List<RelationFile> newrel) {
        List<RelationFile> oldRel = new ArrayList<>(old);
        oldRel.removeAll(newrel);
        return oldRel;
    }

    public static List<RelationFile> getToAdd(List<RelationFile> old, List<RelationFile> newrel) {
        List<RelationFile> newRel = new ArrayList<>(newrel);
        newRel.removeAll(old);
        return newRel;
    }

}
