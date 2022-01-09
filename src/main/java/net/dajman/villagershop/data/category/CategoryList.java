package net.dajman.villagershop.data.category;

import net.dajman.villagershop.common.Pair;
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

    public Optional<Category> getByConfigInventory(final Inventory inventory){
        return this.stream().filter(category -> category.getPageOfConfigInventory(inventory).isPresent()).findFirst();
    }

    public Optional<Pair<Category, Integer>> getCategoryAndPageByConfigInventory(final Inventory inventory){
        return this.stream()
                .map(category -> new Pair<>(category, category.getPageOfConfigInventory(inventory).orElse(null)))
                .filter(Pair::hasValue)
                .findFirst();
    }

}
