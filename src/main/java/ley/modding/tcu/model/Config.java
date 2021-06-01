package ley.modding.tcu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ley.modding.tcu.GiteaAPI;

import java.io.Reader;
import java.io.Writer;

public class Config {

    private static Gson gson = new GsonBuilder().create();

    public static Config fromJSON(Reader reader) {
        return gson.fromJson(reader, Config.class);
    }

    public void toJson(Writer writer) {
        gson.toJson(this, writer);
    }

    public String version;
    public String giteaInstance;
    public String owner;
    public String repository;

    public GiteaAPI getAPI() {
        return new GiteaAPI(giteaInstance, owner, repository);
    }

}
