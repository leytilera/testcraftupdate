package ley.modding.tcu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;

public class Release {

    private static Gson gson = new GsonBuilder().create();

    public static Release[] fromJSON(Reader reader) {
        return gson.fromJson(reader, Release[].class);
    }

    @SerializedName("tag_name")
    public String tag;
    @SerializedName("zipball_url")
    public String zipUrl;

}
