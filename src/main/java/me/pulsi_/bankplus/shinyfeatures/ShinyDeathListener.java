package me.pulsi_.bankplus.shinyfeatures;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ShinyDeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        System.out.println("BANK DEATH");

        FeatureHandler.onDeath(e.getEntity());
    }
}
