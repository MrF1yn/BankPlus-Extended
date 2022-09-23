package me.pulsi_.bankplus.shinyfeatures;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pulsi_.bankplus.BankPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShinyListeners implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getClickedBlock()==null)return;
        if(!(e.getClickedBlock().getState() instanceof Skull))return;
        Skull sk = (Skull) e.getClickedBlock().getState();
        NamespacedKey key = new NamespacedKey(BankPlus.instance(), "money-head");
        PersistentDataContainer dc = sk.getPersistentDataContainer();
        if(!dc.has(key, PersistentDataType.STRING))return;
        e.setCancelled(true);
        String data = dc.get(key, PersistentDataType.STRING);
        String owner = data.split(":")[0];
        double money = Double.parseDouble(data.split(":")[1]);
        BankPlus.instance().getEconomy().depositPlayer(p, money);//adding money
        String msg = BankPlus.instance().features().getString("messages."+"money-pickup");
        if(BankPlus.instance().isPlaceholderAPIHooked())
            msg = PlaceholderAPI.setPlaceholders(p, msg);
        msg = msg.replace("[owner]", owner).replace("[money]", money+"");
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
        p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);
        FeatureHandler.playMoneyPickup(money,p,sk);
        e.getClickedBlock().setType(Material.AIR);
    }
}
