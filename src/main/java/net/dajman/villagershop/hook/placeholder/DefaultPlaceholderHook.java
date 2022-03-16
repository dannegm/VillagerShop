package net.dajman.villagershop.hook.placeholder;

import org.bukkit.OfflinePlayer;

import java.util.List;

public interface DefaultPlaceholderHook {

    default String setPlaceholders(final OfflinePlayer player, final String text){
        return text;
    }

    default List<String> setPlaceholders(final OfflinePlayer player, final List<String> text){
        return text;
    }

    default String setBracketPlaceholders(final OfflinePlayer player, final String text){
        return text;
    }

    default List<String> setBracketPlaceholders(final OfflinePlayer player, final List<String> text){
        return text;
    }

    default String setAllPlaceholders(final OfflinePlayer player, final String text){
        return text;
    }

    default List<String> setAllPlaceholders(final OfflinePlayer player, final List<String> text){
        return text;
    }

}
