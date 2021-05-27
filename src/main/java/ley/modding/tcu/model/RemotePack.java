package ley.modding.tcu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public class RemotePack {

    private static Gson gson = new GsonBuilder().create();

    public static RemotePack fromJSON(Reader reader) {
        return gson.fromJson(reader, RemotePack.class);
    }

    public List<String> versions;
    public String overrides;
    public Map<String, VersionDiff> diff;

}
