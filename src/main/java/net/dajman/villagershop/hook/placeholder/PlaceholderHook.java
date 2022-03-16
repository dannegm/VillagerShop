package net.dajman.villagershop.hook.placeholder;

import net.dajman.villagershop.hook.placeholder.impl.PlaceholderHookImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PlaceholderHook {

    private static DefaultPlaceholderHook placeholderHook;

    private static DefaultPlaceholderHook hook(){
        if (isNull(PlaceholderHook.placeholderHook)){
            final Plugin placeholderPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

            PlaceholderHook.placeholderHook = (nonNull(placeholderPlugin) && placeholderPlugin.isEnabled())
                    ? new PlaceholderHookImpl()
                    : new DefaultPlaceholderHook() {};
        }

        return PlaceholderHook.placeholderHook;
    }

    public static String setPlaceholders(final OfflinePlayer player, final String text){
        return hook().setPlaceholders(player, text);
    }

    public static List<String> setPlaceholders(final OfflinePlayer player, final List<String> text){
        return hook().setPlaceholders(player, text);
    }

    public static String setBracketPlaceholders(final OfflinePlayer player, final String text){
        return hook().setBracketPlaceholders(player, text);
    }

    public static List<String> setBracketPlaceholders(final OfflinePlayer player, final List<String> text){
        return hook().setBracketPlaceholders(player, text);
    }

    public static String setAllPlaceholders(final OfflinePlayer player, final String text){
        return hook().setAllPlaceholders(player, text);
    }

    public static List<String> setAllPlaceholders(final OfflinePlayer player, final List<String> text){
        return hook().setAllPlaceholders(player, text);
    }
}
