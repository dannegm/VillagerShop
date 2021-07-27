package net.dajman.villagershop.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorUtil {

    public static String fixColors(String text){
        return ChatColor.translateAlternateColorCodes('&', text.replace("<%", "«").replace("%>", "»"));
    }

    public static List<String> fixColors(List<String> list){
        final List<String> fixed = new ArrayList<String>();
        if (list == null || list.isEmpty()){
            return fixed;
        }
        for(String s : list){
            fixed.add(fixColors(s));
        }
        return fixed;
    }

}
