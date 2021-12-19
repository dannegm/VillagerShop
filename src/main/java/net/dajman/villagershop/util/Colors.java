package net.dajman.villagershop.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class Colors {

    public static String fixColors(String text){

        if (isNull(text)){
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', text
                .replace("<%", "«")
                .replace("%>", "»")
        );

    }

    public static List<String> fixColors(final List<String> list){

        final List<String> fixed = new ArrayList<String>();

        if (isNull(list) || list.isEmpty()){
            return fixed;
        }

        for(String s : list){
            fixed.add(fixColors(s));
        }
        return fixed;
    }

}
