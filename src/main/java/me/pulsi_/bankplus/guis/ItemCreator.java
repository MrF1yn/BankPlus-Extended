package me.pulsi_.bankplus.guis;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pulsi_.bankplus.BankPlus;
import me.pulsi_.bankplus.managers.EconomyManager;
import me.pulsi_.bankplus.utils.ChatUtils;
import me.pulsi_.bankplus.utils.HeadUtils;
import me.pulsi_.bankplus.utils.MethodUtils;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemCreator {

    private static final ItemStack barrier = new ItemStack(Material.BARRIER);
    
    public static ItemStack createItemStack(ConfigurationSection c, Player p) {

        final BankPlus plugin = JavaPlugin.getPlugin(BankPlus.class);
        final EconomyManager economy = new EconomyManager(plugin);
        final int cooldown = plugin.players().getInt("Interest-Cooldown");

        ItemStack item;
        final String material = c.getString("Material");
        if (material.contains("HEAD-%PLAYER%")) {
            try {
                item = HeadUtils.getNameHead(p.getName(), new ItemStack(Material.PLAYER_HEAD));
            } catch (NoSuchFieldError er) {
                item = HeadUtils.getNameHead(p.getName(), new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal()));
            } catch (IllegalArgumentException e) {
                item = barrier;
            }
            return item;
        }
        if (material.startsWith("HEAD[")) {
            final String player = c.getString("Material").replace("HEAD[", "").replace("]", "");
            try {
                item = HeadUtils.getNameHead(player, new ItemStack(Material.PLAYER_HEAD));
            } catch (NoSuchFieldError er) {
                item = HeadUtils.getNameHead(player, new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal()));
            } catch (IllegalArgumentException e) {
                item = barrier;
            }
            return item;
        }
        if (material.startsWith("HEAD-<")) {
            final String textureValue = c.getString("Material").replace("HEAD-<", "").replace(">", "");
            try {
                item = HeadUtils.getValueHead(new ItemStack(Material.PLAYER_HEAD), textureValue);
            } catch (NoSuchFieldError er) {
                item = HeadUtils.getValueHead(new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal()), textureValue);
            } catch (IllegalArgumentException e) {
                item = barrier;
            }
            return item;
        }

        try {
            if (material.contains(":")) {
                String[] itemData = material.split(":");
                item = new ItemStack(Material.valueOf(itemData[0]), 1, Byte.parseByte(itemData[1]));
            } else {
                item = new ItemStack(Material.valueOf(material));
            }
        } catch (IllegalArgumentException e) {
            item = barrier;
        }

        ItemMeta itemMeta = item.getItemMeta();


        final String displayName = c.getString("DisplayName");
        if (displayName == null) {
            itemMeta.setDisplayName(ChatUtils.color("&c&l*CANNOT FIND DISPLAYNAME*"));
        } else {
            if (plugin.isPlaceholderAPIHooked()) {
                itemMeta.setDisplayName(ChatUtils.color(PlaceholderAPI.setPlaceholders(p, displayName)));
            } else {
                itemMeta.setDisplayName(ChatUtils.color(displayName));
            }
        }

        List<String> lore = new ArrayList<>();
        for (String lines : c.getStringList("Lore")) {
            lore.add(ChatUtils.color(lines
                    .replace("%player_name%", p.getName())
                    .replace("%balance%", MethodUtils.formatCommas(economy.getBankBalance(p)))
                    .replace("%balance_formatted%", MethodUtils.format(economy.getBankBalance(p), plugin))
                    .replace("%balance_formatted_long%", MethodUtils.formatLong(economy.getBankBalance(p), plugin))
                    .replace("%interest_cooldown%", MethodUtils.formatTime(cooldown, plugin))
            ));
        }
        if (plugin.isPlaceholderAPIHooked()) {
            itemMeta.setLore(PlaceholderAPI.setPlaceholders(p, lore));
        } else {
            itemMeta.setLore(lore);
        }

        if (c.getBoolean("Glowing")) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack guiFiller() {
        BankPlus plugin = JavaPlugin.getPlugin(BankPlus.class);
        ItemStack filler;
        try {
            final String material = plugin.config().getString("Gui.Filler.Material");
            if (material.contains(":")) {
                String[] itemData = material.split(":");
                filler = new ItemStack(Material.valueOf(itemData[0]), 1, Byte.parseByte(itemData[1]));
            } else {
                filler = new ItemStack(Material.valueOf(material));
            }
        } catch (IllegalArgumentException e) {
            filler = barrier;
        }

        ItemMeta fillerMeta = filler.getItemMeta();

        final String displayName = plugin.config().getString("Gui.Filler.DisplayName");
        if (displayName == null) {
            fillerMeta.setDisplayName(ChatUtils.color("&c&l*CANNOT FIND DISPLAYNAME*"));
        } else {
            fillerMeta.setDisplayName(ChatUtils.color(displayName));
        }

        if (plugin.config().getBoolean("Gui.Filler.Glowing")) {
            fillerMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            filler.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        filler.setItemMeta(fillerMeta);
        return filler;
    }

    public static ItemMeta setLore(ConfigurationSection c, ItemStack i, Player p) {
        final BankPlus plugin = JavaPlugin.getPlugin(BankPlus.class);
        final ItemMeta itemMeta = i.getItemMeta();

        List<String> lore = new ArrayList<>();
        for (String lines : c.getStringList("Lore"))
            lore.add(ChatUtils.color(lines));
        if (plugin.isPlaceholderAPIHooked()) {
            itemMeta.setLore(PlaceholderAPI.setPlaceholders(p, lore));
        } else {
            itemMeta.setLore(lore);
        }
        return itemMeta;
    }
}