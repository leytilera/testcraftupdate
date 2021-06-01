package ley.modding.tcu;

import ley.anvil.addonscript.v1.AddonscriptJSON;
import ley.modding.tcu.model.Release;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiteaAPI {

    String baseURL;
    String repo;
    String owner;

    public GiteaAPI(String baseURL, String owner, String repo) {
        this.baseURL = baseURL;
        this.repo = repo;
        this.owner = owner;
        if (!this.baseURL.endsWith("/")) {
            this.baseURL += "/";
        }
    }

    public AddonscriptJSON getASFromTag(String tag) {
        String url = baseURL + owner + "/" + repo + "/raw/tag/" + tag + "/src/modpack.json";
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            if (con.getResponseCode() != 200) {
                return null;
            }
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            AddonscriptJSON as = AddonscriptJSON.read(reader);
            reader.close();
            return as;
        } catch (IOException e) {
            return null;
        }
    }

    public List<Release> getReleases() {
        String url = baseURL + "api/v1/repos/" + owner + "/" + repo + "/releases";
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            if (con.getResponseCode() != 200) {
                return new ArrayList<>();
            }
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            Release[] releases = Release.fromJSON(reader);
            reader.close();
            return Arrays.asList(releases);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


}
