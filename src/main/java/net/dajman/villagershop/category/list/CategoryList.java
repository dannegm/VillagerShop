package net.dajman.villagershop.category.list;

import net.dajman.villagershop.category.Category;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CategoryList extends ArrayList<Category> {

    @Override
    public boolean add(Category category) {
        if (this.contains(category)){
            return false;
        }
        return super.add(category);
    }

    @Nullable
    public Category get(final String name){
        for (Category category : this) {
            if (category.getPath().equalsIgnoreCase(name) || category.getName().equalsIgnoreCase(name)){
                return category;
            }
        }
        return null;
    }

    @Nullable
    public Category get(final int slot){
        for (Category category : this) {
            if (category.getSlot() == slot){
                return category;
            }
        }
        return null;
    }

    @Nullable
    public Category get(final Inventory inventory){
        for (Category category : this) {
            if (category.getConfigInventory().equals(inventory)){
                return category;
            }
        }
        return null;
    }

}
