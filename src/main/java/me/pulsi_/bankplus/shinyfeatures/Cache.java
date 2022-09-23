package me.pulsi_.bankplus.shinyfeatures;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.pulsi_.bankplus.BankPlus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.net.URL;
import java.util.*;

public class Cache {
    public HashMap<String, PlayerProfile> skinProfiles;
    public Cache(){
        skinProfiles = new HashMap<>();
    }

    public void initCache() {
        skinProfiles.clear();
        try {
            for (String s : BankPlus.instance().features().getConfigurationSection("death-drop.groups").getKeys(false)) {
                String url = BankPlus.instance().features().getString("death-drop.groups." + s + ".head-texture-url");
                if (!skinProfiles.containsKey(url)) {
                    PlayerProfile pp = Bukkit.createProfile(UUID.randomUUID());
                    pp.setProperty(new ProfileProperty("textures", url));
                    pp.complete();
                    skinProfiles.put(url, pp);
                }
            }
            for (String s : BankPlus.instance().features().getConfigurationSection("random-drop.groups").getKeys(false)) {
                String url = BankPlus.instance().features().getString("random-drop.groups." + s + ".head-texture-url");
                if (!skinProfiles.containsKey(url)) {
                    PlayerProfile pp = Bukkit.createProfile(UUID.randomUUID());
                    pp.setProperty(new ProfileProperty("textures", url));
                    pp.complete();
                    skinProfiles.put(url, pp);
                }
            }
        } catch (Exception e) {

        }
    }

}
