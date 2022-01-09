package net.dajman.villagershop.inventory.common;

import org.bukkit.Material;

public class Materials {

    private static Material OAK_FENCE_GATE_MATERIAL;
    private static Material GRAY_STAINED_GLASS_PANE;

    static{

        try{
            Materials.OAK_FENCE_GATE_MATERIAL = Material.valueOf("OAK_FENCE_GATE");
            Materials.GRAY_STAINED_GLASS_PANE = Material.valueOf("GRAY_STAINED_GLASS_PANE");

        } catch (IllegalArgumentException e){
            Materials.OAK_FENCE_GATE_MATERIAL = Material.valueOf("FENCE_GATE");
            Materials.GRAY_STAINED_GLASS_PANE = Material.valueOf("STAINED_GLASS_PANE");
        }
    }

    public static Material OAK_FENCE_GATE_MATERIAL(){
        return Materials.OAK_FENCE_GATE_MATERIAL;
    }

    public static Material GRAY_STAINED_GLASS_PANE(){
        return Materials.GRAY_STAINED_GLASS_PANE;
    }

}
