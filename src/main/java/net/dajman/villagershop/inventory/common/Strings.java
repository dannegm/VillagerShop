package net.dajman.villagershop.inventory.common;

import net.dajman.villagershop.util.Colors;

public class Strings {

    private static final String CONFIG_INVENTORY_TITLE_PREFIX = "Page: ";
    private static final String CONFIG_INVENTORY_NEXT_PAGE_ITEM_NAME = Colors.fixColors("&cNext page");
    private static final String CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_NAME = Colors.fixColors("&cPrevious page");

    public static String CONFIG_INVENTORY_NEXT_PAGE_ITEM_NAME() {
        return Strings.CONFIG_INVENTORY_NEXT_PAGE_ITEM_NAME;
    }

    public static String CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_NAME() {
        return Strings.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_NAME;
    }

    public static String CONFIG_INVENTORY_TITLE_PREFIX(){
        return Strings.CONFIG_INVENTORY_TITLE_PREFIX;
    }
}
