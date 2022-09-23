package me.pulsi_.bankplus.shinyfeatures;

import me.pulsi_.bankplus.BankPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RandomDrop {
    private BukkitTask task;
    private long interval;

    public RandomDrop(long interval){
        this.interval = interval;
    }

    public void startTask(){
        if(this.task!=null) {
            task.cancel();
            task = null;
        }
        task = new BukkitRunnable(){
            @Override
            public void run(){
                for(Player p : Bukkit.getOnlinePlayers()){
                    FeatureHandler.onRandomDrop(p);
                }
            }
        }.runTaskTimer(BankPlus.instance(), interval*20, interval*20);
    }

    public void stopTask(){
        if(this.task!=null) {
            task.cancel();
            task = null;
        }
    }
}
