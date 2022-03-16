package net.dajman.villagershop.hook.placeholder.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dajman.villagershop.hook.placeholder.DefaultPlaceholderHook;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PlaceholderHookImpl implements DefaultPlaceholderHook {

    @Override
    public String setPlaceholders(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public List<String> setPlaceholders(OfflinePlayer player, List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public String setBracketPlaceholders(OfflinePlayer player, String text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public List<String> setBracketPlaceholders(OfflinePlayer player, List<String> text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public String setAllPlaceholders(OfflinePlayer player, String text) {
        return this.setPlaceholders(player, this.setBracketPlaceholders(player, text));
    }

    @Override
    public List<String> setAllPlaceholders(OfflinePlayer player, List<String> text) {
        return this.setPlaceholders(player, this.setBracketPlaceholders(player, text));
    }
}
