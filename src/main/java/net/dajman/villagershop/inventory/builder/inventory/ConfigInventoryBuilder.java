package net.dajman.villagershop.inventory.builder.inventory;

import net.dajman.villagershop.common.Builder;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.common.Items;
import net.dajman.villagershop.inventory.common.Strings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ConfigInventoryBuilder implements Builder<Inventory> {

    private static final Logger LOGGER = Logger.getLogger(ConfigInventoryBuilder.class);

    private final int page;

    public ConfigInventoryBuilder(final int page) {
        this.page = page;
    }

    @Override
    public Inventory build() {

        if (this.page < 0){
            LOGGER.error("build() Given page={} is less than 0.");
            return null;
        }

        final Inventory inventory = Bukkit.createInventory(null, 36,
                Strings.CONFIG_INVENTORY_TITLE_PREFIX() + (this.page + 1));

        inventory.setItem(27, Items.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_STACK());
        inventory.setItem(35, Items.CONFIG_INVENTORY_NEXT_PAGE_ITEM_STACK());

        final ItemStack grayStainedGlassPane = Items.GRAY_STAINED_GLASS_PANE_ITEM_STACK();

        for(int i = 28; i < 35; i++){
            inventory.setItem(i, grayStainedGlassPane);
        }

        return inventory;
    }
}
