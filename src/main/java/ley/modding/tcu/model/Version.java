package ley.modding.tcu.model;

public class Version {

    public String latest;
    public String current;
    public String overrides;

    public Version(String latest, String current, String overrides) {
        this.latest = latest;
        this.current = current;
        this.overrides = overrides;
    }

    public boolean isLatest() {
        return latest.equals(current);
    }

}
