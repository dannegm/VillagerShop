package net.dajman.villagershop.category.list;

import net.dajman.villagershop.category.Category;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Optional;

public class CategoryList extends ArrayList<Category> {


    public boolean add(Category category) {
        if (this.contains(category)){
            return false;
        }
        return super.add(category);
    }

    public Optional<Category> getByName(final String name){
        return this.stream().filter(category ->
                category.getPath().equalsIgnoreCase(name)
                        || ChatColor.stripColor(category.getName()).equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<Category> getBySlot(final int slot){
        return this.stream().filter(category -> category.getSlot() == slot).findFirst();
    }

    public Optional<Category> getByInventory(final Inventory inventory){
        return this.stream().filter(category -> category.getConfigInventory().equals(inventory)).findFirst();
    }

}
