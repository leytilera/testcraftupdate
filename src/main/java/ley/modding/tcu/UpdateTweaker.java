package ley.modding.tcu;

import ley.modding.tcu.model.LocalPack;
import ley.modding.tcu.model.VersionDiff;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.net.URL;
import java.util.List;

public class UpdateTweaker implements ITweaker {

    private List<String> args;
    private File gameDir;
    private File assetsDir;
    private String profile;
    private LaunchClassLoader classLoader;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args = args;
        this.gameDir = gameDir;
        this.assetsDir = assetsDir;
        this.profile = profile;
        if(gameDir == null) {
            this.gameDir = new File(".").getAbsoluteFile();
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        this.classLoader = classLoader;
        try {
            File modpack = new File(gameDir.getAbsolutePath() + "/modpack.json");
            if (modpack.exists()) {
                String[] ver = Util.checkVersion(modpack);
                if (ver.length == 0)
                    return;
                VersionDiff diff = Util.buildDiff(ver);
                FileHandler handler = new FileHandler(gameDir);
                handler.processDiff(diff);
                URL overrides = Util.getOverrides();
                if (overrides != null)
                    handler.processOverrides(overrides);
                LocalPack pack = Util.getLocal(modpack);
                pack.version = ver[ver.length - 1];
                Util.writeLocal(modpack);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    public void callInjectedTweaker(ITweaker tweaker) {
        tweaker.acceptOptions(args, gameDir, assetsDir, profile);
        tweaker.injectIntoClassLoader(classLoader);
    }

}
