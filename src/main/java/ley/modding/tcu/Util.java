package ley.modding.tcu;

import ley.modding.tcu.model.LocalPack;
import ley.modding.tcu.model.RemotePack;
import ley.modding.tcu.model.VersionDiff;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Util {

    private static RemotePack rcache = null;
    private static LocalPack lcache = null;

    public static RemotePack getRemote(LocalPack pack) throws IOException {
        if (rcache != null)
            return rcache;
        URL url = new URL(pack.packURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        int status = con.getResponseCode();
        if (status != 200) {
            return null;
        }
        InputStreamReader r2 = new InputStreamReader(con.getInputStream());
        RemotePack ret = RemotePack.fromJSON(r2);
        r2.close();
        rcache = ret;
        return ret;
    }

    public static LocalPack getLocal(File local) throws IOException {
        if (lcache != null)
            return lcache;
        Reader r = new FileReader(local);
        LocalPack pack = LocalPack.fromJSON(r);
        r.close();
        lcache = pack;
        return pack;
    }

    public static void writeLocal(File local) throws IOException {
        Writer w = new FileWriter(local);
        lcache.toJson(w);
        w.close();
    }

    public static String[] checkVersion(File local) throws IOException {
        LocalPack lpack = getLocal(local);

        RemotePack rpack = getRemote(lpack);

        if (rpack != null && rpack.versions.contains(lpack.version)) {
            int index = rpack.versions.indexOf(lpack.version);
            int lastIndex = rpack.versions.size() - 1;
            if (index == lastIndex) {
                return new String[0];
            } else  {
                String[] ret = new String[lastIndex - index];
                int j = 0;
                for (int i = index + 1; i <= lastIndex; i++) {
                    ret [j] = rpack.versions.get(i);
                    j++;
                }
                return ret;
            }
        }
        return new String[0];
    }

    public static VersionDiff buildDiff(String[] versions) throws IOException {
        VersionDiff diff = new VersionDiff();
        diff.add = new ArrayList<>();
        diff.remove = new ArrayList<>();
        RemotePack pack = getRemote(lcache);

        for (String ver : versions) {
            VersionDiff vdiff = pack.diff.get(ver);
            if (vdiff != null) {
                if (vdiff.remove != null) {
                    for (VersionDiff.RemoveFile f : vdiff.remove) {
                        boolean add = true;
                        List<VersionDiff.AddFile> toRem = new ArrayList<>();
                        for (VersionDiff.AddFile a : diff.add) {
                            if (a.dir.equals(f.dir) && a.filename.equals(f.filename)) {
                                add = false;
                                toRem.add(a);
                            }
                        }
                        diff.add.removeAll(toRem);
                        if (add)
                            diff.remove.add(f);
                    }
                }
                if (vdiff.add != null)
                    diff.add.addAll(vdiff.add);
            }
        }

        return diff;
    }

    public static URL getOverrides() {
        if (rcache != null && rcache.overrides != null && !rcache.overrides.isEmpty()) {
            try {
                return new URL(rcache.overrides);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
