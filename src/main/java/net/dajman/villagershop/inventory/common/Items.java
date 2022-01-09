package net.dajman.villagershop.inventory.common;

import net.dajman.villagershop.inventory.builder.itemstack.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class Items {

    private static ItemBuilder CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_BUILDER;
    private static ItemBuilder CONFIG_INVENTORY_NEXT_PAGE_ITEM_BUILDER;

    private static ItemBuilder GRAY_STAINED_GLASS_PANE;

    static{
        Items.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_BUILDER = new ItemBuilder(Materials.OAK_FENCE_GATE_MATERIAL())
                .setName(Strings.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_NAME());
        Items.CONFIG_INVENTORY_NEXT_PAGE_ITEM_BUILDER = new ItemBuilder(Materials.OAK_FENCE_GATE_MATERIAL())
                .setName(Strings.CONFIG_INVENTORY_NEXT_PAGE_ITEM_NAME());

        Items.GRAY_STAINED_GLASS_PANE = new ItemBuilder(Materials.GRAY_STAINED_GLASS_PANE(), 1, (byte)7);
    }

    public static ItemBuilder CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_BUILDER(){
        return Items.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_BUILDER;
    }

    public static ItemStack CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_STACK(){
        return Items.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_BUILDER.build();
    }

    public static ItemBuilder CONFIG_INVENTORY_NEXT_PAGE_ITEM_BUILDER(){
        return Items.CONFIG_INVENTORY_NEXT_PAGE_ITEM_BUILDER;
    }

    public static ItemStack CONFIG_INVENTORY_NEXT_PAGE_ITEM_STACK(){
        return Items.CONFIG_INVENTORY_NEXT_PAGE_ITEM_BUILDER.build();
    }

    public static ItemBuilder GRAY_STAINED_GLASS_PANE_ITEM_BUILDER(){
        return Items.GRAY_STAINED_GLASS_PANE;
    }

    public static ItemStack GRAY_STAINED_GLASS_PANE_ITEM_STACK(){
        return Items.GRAY_STAINED_GLASS_PANE.build();
    }

}
