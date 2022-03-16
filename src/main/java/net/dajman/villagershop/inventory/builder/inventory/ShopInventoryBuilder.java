package net.dajman.villagershop.inventory.builder.inventory;

import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.data.category.CategoryList;
import net.dajman.villagershop.hook.placeholder.PlaceholderHook;
import net.dajman.villagershop.inventory.builder.itemstack.ItemBuilder;
import net.dajman.villagershop.common.Builder;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.common.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.dajman.villagershop.inventory.common.Strings.SHOP_INVENTORY_SUFFIX;

public class ShopInventoryBuilder implements Builder<Inventory> {

    private static final Logger LOGGER = Logger.getLogger(ShopInventoryBuilder.class);

    private final String guiName;
    private final int guiRows;
    private final CategoryList categories;
    private final ItemBuilder fillItem;
    private final OfflinePlayer offlinePlayer;

    public ShopInventoryBuilder(final String guiName, final int guiRows, final CategoryList categories,
                                final ItemBuilder fillItem, final OfflinePlayer offlinePlayer){
        this.guiName = guiName;
        this.guiRows = guiRows;
        this.categories = categories;
        this.fillItem = fillItem;
        this.offlinePlayer = offlinePlayer;
    }

    public void open(final Player player){
        player.openInventory(this.build());
    }

    @Override
    public Inventory build(){

        if (isNull(this.guiName)){
            LOGGER.error("build() GuiName is null");
            return null;
        }

        if (this.guiRows < 1 || this.guiRows > 6){
            LOGGER.error("build() Incorrect gui rows number {}, should be between 1 and 6.");
            return null;
        }

        if (isNull(this.categories)){
            LOGGER.error("build() Categories list is null");
            return null;
        }

        if (isNull(this.fillItem)){
            LOGGER.error("build() Fill item is null");
            return null;
        }

        final String guiName = PlaceholderHook.setAllPlaceholders(this.offlinePlayer, this.guiName) + SHOP_INVENTORY_SUFFIX();

        final Inventory inventory = Bukkit.createInventory(null, this.guiRows *  9, guiName);

        for (Category category : this.categories) {

            final ItemBuilder categoryItem = category.getItem().clone();
            categoryItem.setName(PlaceholderHook.setAllPlaceholders(this.offlinePlayer, categoryItem.getName()));
            categoryItem.setLore(PlaceholderHook.setAllPlaceholders(this.offlinePlayer, categoryItem.getLore()));

            inventory.setItem(category.getSlot(), categoryItem.build());
        }

        final ItemBuilder fillItem = this.fillItem.clone();
        fillItem.setName(PlaceholderHook.setAllPlaceholders(this.offlinePlayer, fillItem.getName()));
        fillItem.setLore(PlaceholderHook.setAllPlaceholders(this.offlinePlayer, fillItem.getLore()));

        final ItemStack fillItemStack = fillItem.build();

        if (isNull(fillItemStack) || Objects.equals(fillItemStack.getType(), Material.AIR)){
            return inventory;
        }

        for(int i = 0; i < inventory.getSize(); i++){

            final ItemStack itemStack = inventory.getItem(i);

            if (nonNull(itemStack) && !Objects.equals(itemStack.getType(), Material.AIR)){
                continue;
            }

            inventory.setItem(i, fillItemStack);
        }

        return inventory;
    }

}
