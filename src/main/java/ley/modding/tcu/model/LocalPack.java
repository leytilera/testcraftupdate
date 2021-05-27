package ley.modding.tcu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;

public class LocalPack {

    private static Gson gson = new GsonBuilder().create();

    public static LocalPack fromJSON(Reader reader) {
        return gson.fromJson(reader, LocalPack.class);
    }

    public void toJson(Writer writer) {
        gson.toJson(this, writer);
    }

    public String version;
    public String packURL;

}
