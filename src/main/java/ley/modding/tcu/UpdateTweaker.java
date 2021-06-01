package ley.modding.tcu;

import ley.anvil.addonscript.v1.AddonscriptJSON;
import ley.modding.tcu.model.Config;
import ley.modding.tcu.model.RelationFile;
import ley.modding.tcu.model.Version;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            File configfile = new File(gameDir.getAbsolutePath() + "/pack.json");
            if (configfile.exists()) {
                Config config = Util.getConfig(configfile);
                Version version = Util.checkVersion(config);
                if (version.isLatest())
                    return;
                System.out.println(version.current + " is outdated, starting update...");
                AddonscriptJSON oldAS = config.getAPI().getASFromTag(version.current);
                AddonscriptJSON newAS = config.getAPI().getASFromTag(version.latest);
                List<RelationFile> oldRel = Util.getRelations(oldAS);
                List<RelationFile> newRel = Util.getRelations(newAS);
                FileHandler handler = new FileHandler(gameDir);
                handler.removeFiles(Util.getToRemove(oldRel, newRel));
                handler.addFiles(Util.getToAdd(oldRel, newRel));
                handler.processOverrides(version.overrides, newAS, config);
                config.version = version.latest;
                FileWriter writer = new FileWriter(configfile);
                config.toJson(writer);
                writer.close();
            }
        } catch (IOException e) {
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
