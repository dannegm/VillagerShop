package net.dajman.villagershop.data.category;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.inventory.builder.itemstack.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class Category {

    private static final String CATEGORY_PERMISSION_PREFIX = Main.PERMISSION_PREFIX + "category.";

    private String path;
    private String name;
    private int slot;
    private ItemBuilder item;
    private List<Inventory> configInventories;


    public Category(final String path, final String name, final int slot, final ItemBuilder itemBuilder){
        this.path = path;
        this.name = name;
        this.slot = slot;
        this.item = itemBuilder;
        this.configInventories = new ArrayList<>();
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

    public Optional<Integer> getPageOfConfigInventory(final Inventory inventory){

        for(int i = 0; i < this.configInventories.size(); i++){

            if (this.configInventories.get(i).equals(inventory)){
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public List<Inventory> getConfigInventories() {
        return nonNull(this.configInventories)
                ? new ArrayList<>(this.configInventories)
                : null;
    }

    public Optional<Inventory> getConfigInventory(final int page){

        if (this.configInventories.size() <= page){
            return Optional.empty();
        }

        return Optional.of(this.configInventories.get(page));
    }

    public boolean addConfigInventory(final Inventory inventory){

        if (this.configInventories.contains(inventory)){
            return false;
        }

        return this.configInventories.add(inventory);
    }

    public boolean removeConfigInventory(final Inventory inventory){
        return this.configInventories.remove(inventory);
    }

    public void setConfigInventories(final List<Inventory> inventories){

        this.configInventories.forEach(inventory -> inventory.getViewers()
                .forEach(HumanEntity::closeInventory));

        this.configInventories = nonNull(inventories)
                ? new ArrayList<>(inventories)
                : null;
    }

    public void clearConfigurationInventories(){
        this.configInventories.clear();;
    }

    public Integer getAllConfigInventoryViewers(){
        return this.configInventories.stream().mapToInt(inventory -> inventory.getViewers().size()).sum();
    }

    public String getPermission(){
        return CATEGORY_PERMISSION_PREFIX + this.getPath();
    }

    @Override
    public String toString() {
        return "Category{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", slot=" + slot +
                '}';
    }
}
