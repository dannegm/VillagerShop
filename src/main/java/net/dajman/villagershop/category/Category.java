package net.dajman.villagershop.category;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.inventory.itemstack.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Category {

    private static final String CATEGORY_PERMISSION_PREFIX = Main.PERMISSION_PREFIX + "category.";

    private String path;
    private String name;
    private int slot;
    private ItemBuilder item;
    private Inventory configInventory;

    public Category(final String path, final String name, final int slot, final ItemBuilder itemBuilder){
        this.path = path;
        this.name = name;
        this.slot = slot;
        this.item = itemBuilder;
        this.configInventory = Bukkit.createInventory(null, 54);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemBuilder getItem() {
        return item;
    }

    public void setItem(ItemBuilder item) {
        this.item = item;
    }

    public Inventory getConfigInventory() {
        return configInventory;
    }

    public void setConfigInventory(Inventory configInventory) {
        this.configInventory = configInventory;
    }

    public String getPermission(){
        return CATEGORY_PERMISSION_PREFIX + this.getPath();
    }
}
