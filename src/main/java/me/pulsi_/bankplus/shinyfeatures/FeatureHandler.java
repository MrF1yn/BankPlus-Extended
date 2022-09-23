package me.pulsi_.bankplus.shinyfeatures;

import de.slikey.effectlib.effect.*;
import me.pulsi_.bankplus.BankPlus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


public class FeatureHandler {
    public static void onDeath(Player p){
        if(p.hasPermission("bankplus.deathdrop.bypass"))return;
        if(BankPlus.instance().getEconomy().getBalance(p)<1.0)return;
        if(!WorldGuardIntegration.shouldSpawnHead(p))return;
        for(String s : BankPlus.instance().features().getConfigurationSection("death-drop.groups").getKeys(false)){
            if(p.hasPermission("bankplus.deathdropgroup."+s)){
                double randomDropPercent = parseRandomRange(BankPlus.instance().features().getString("death-drop.groups."+s+".drop-percent"));
                double completeLossPercent = parseRandomRange(BankPlus.instance().features().getString("death-drop.groups."+s+".complete-loss-percent"));
                double droppedAmount = (randomDropPercent/100.0)*BankPlus.instance().getEconomy().getBalance(p);
                BankPlus.instance().getEconomy().withdrawPlayer(p, droppedAmount);//deducting money
                double completeLossAmount = (completeLossPercent/100.0)*droppedAmount;

                double finalDropAmount = droppedAmount-completeLossAmount;
//                System.out.println("SPAWN TRIGGERED "+s);
                if(BankPlus.instance().features().getBoolean("death-drop.groups."+s+".drop-as-head"))
                    spawnMoneyHead(p, finalDropAmount, BankPlus.instance().features().getString("death-drop.groups."+s+".head-texture-url"), true);
                //TODO SPAWN HEAD
                return;
            }
        }
    }

    public static void onRandomDrop(Player p){
        if(p.hasPermission("bankplus.randomdrop.bypass"))return;
        if(BankPlus.instance().getEconomy().getBalance(p)<1.0)return;
        if(!WorldGuardIntegration.shouldSpawnHead(p))return;
        for(String s : BankPlus.instance().features().getConfigurationSection("random-drop.groups").getKeys(false)){
            if(p.hasPermission("bankplus.randomdropgroup."+s)){
                int shouldDropPercent = parseRandomRangeInt(BankPlus.instance().features().getString("random-drop.groups."+s+".should-drop-percent"));
                if(!shouldDrop(shouldDropPercent))return;
                double randomDropPercent = parseRandomRange(BankPlus.instance().features().getString("random-drop.groups."+s+".drop-percent"));
                double droppedAmount = (randomDropPercent/100.0)*BankPlus.instance().getEconomy().getBalance(p);
                BankPlus.instance().getEconomy().withdrawPlayer(p, droppedAmount);//deducting money
//                System.out.println("SPAWN TRIGGERED RANDOM "+s);
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
                if(BankPlus.instance().features().getBoolean("random-drop.groups."+s+".drop-as-head"))
                    spawnMoneyHead(p, droppedAmount, BankPlus.instance().features().getString("random-drop.groups."+s+".head-texture-url"), false);
                //TODO SPAWN HEAD
                return;
            }
        }
    }

    public static void playMoneyPickup(double money, Player p, Skull sk){
        NewTextEffect effect = new NewTextEffect(BankPlus.instance().elib);
        effect.text = BankPlus.instance().features().getString("messages.money-float-effect").replace("[money]", String.format("%.1f", money));
        effect.setPlayer(p);
        effect.particle = Particle.FLAME;
        effect.particleCount = 0;
        effect.iterations=1;
        effect.size= (float) 1/20;
        effect.setLocation(sk.getLocation().add(0,1,0).setDirection(p.getLocation().getDirection()));
        effect.start();
    }

    public static void playMoneyPickup1(double money, Player p, Skull sk) {
        SkyRocketEffect effect = new SkyRocketEffect(BankPlus.instance().elib);
//        effect.particle = Particle.FLAME;
        effect.particleCount = 0;
//        effect.iterations = ;
//        effect.size = (float) 1 / 20;
        effect.setLocation(sk.getLocation().add(0, 8, 0).setDirection(p.getLocation().getDirection()));
        effect.start();
    }

    public static void spawnMoneyHead(Player p, double money, String url, boolean safeSpawn){
        try {
            NamespacedKey key = new NamespacedKey(BankPlus.instance(), "money-head");
            Location safeLoc = p.getLocation();
            if(safeSpawn) {
                safeLoc = getSafeBlock(p);
                if (safeLoc == null) return;
            }
            else {
                if(safeLoc.getBlock().getType()!=Material.AIR){
                    safeLoc = safeLoc.add(0,1,0);
                }
                if(safeLoc.getBlock().getType()!=Material.AIR)return;
            }
            Block b = safeLoc.getBlock();
            b.setType(Material.PLAYER_HEAD);
            Skull head = (Skull) b.getState();
            head.setPlayerProfile(BankPlus.instance().cache.skinProfiles.get(url));
            head.setRotation(BlockFace.NORTH_EAST);
            head.getPersistentDataContainer().set(key, PersistentDataType.STRING, p.getName()+":"+String.format("%.1f", money));
            head.update();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&aYou dropped money at: &e"+safeLoc.getBlockX()+" "+safeLoc.getBlockY()+" "+safeLoc.getBlockZ()+"."));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Location getSafeBlock(Player p){
        Block b=p.getWorld().getHighestBlockAt(p.getLocation().add(1,0,0));
        if(b.getWorld().getEnvironment() == World.Environment.NETHER&&p.getLocation().getY()<127){
            for(int y = b.getY(); y>0; y--){
                Block c = new Location(p.getWorld(), b.getX(), y, b.getZ()).getBlock();
                Block c1 = new Location(p.getWorld(), b.getX(), y-1, b.getZ()).getBlock();
                if(c1.getType()!=Material.AIR&&c.getType()==Material.AIR){
                    return c.getLocation();
                }
            }
            return null;
        }
        b = b.getLocation().add(0,1,0).getBlock();
        if(b.getType()!=Material.AIR&&b.getType()!=Material.LAVA&&b.getType()!=Material.WATER&&b.getType()!=Material.TALL_GRASS&&b.getType()!=Material.GRASS)
            return null;
//        System.out.println(b.getLocation());
        return b.getLocation();
    }

    public static double parseRandomRange(String s){
        String[] str = s.split("-");
        if(str.length==2){
            double max = Math.max(Double.parseDouble(str[0]), Double.parseDouble(str[1]));
            double min = Math.min(Double.parseDouble(str[0]), Double.parseDouble(str[1]));
            return ((Math.random() * (max - min)) + min);
        }else if(str.length==1){
            return Double.parseDouble(str[0]);
        }
        return 0.0;
    }
    public static int parseRandomRangeInt(String s){
        String[] str = s.split("-");
        if(str.length==2){
            int max = Math.max(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
            int min = Math.min(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
            return (int) ((Math.random() * (max - min)) + min);
        }else if(str.length==1){
            return Integer.parseInt(str[0]);
        }
        return 0;
    }

    public static boolean shouldDrop(int percent) {
        int random = (int)((Math.random() * (100)) + 0);
        return random >= percent;
    }
    public static ItemStack customPlayerHead(String skin, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        //item = Bukkit.getUnsafe().modifyItemStack(head, dev.MrFlyn.shopkeeperNavAddon.Main.plugin.getConfig().getString("backpack." + String.valueOf(lev) + ".skin"));
        head = Bukkit.getUnsafe().modifyItemStack(head, "{display:{Name:\"{\\\"text\\\":\\\"Pumpkin Bowl\\\"}\"},SkullOwner:{Id:[" + "I;1201296705,1414024019,-1385893868,1321399054" + "],Properties:{textures:[{Value:\"" + skin + "\"}]}}}");
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(name);
        head.setItemMeta(meta);
        return head;
    }
}
