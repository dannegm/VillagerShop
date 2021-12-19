package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InventoryCloseListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger(InventoryCloseListener.class);

    private final Main plugin;

    public InventoryCloseListener(Main plugin) {
        this.plugin = plugin;

        LOGGER.debug("InventoryCloseListener() Initialized");
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e){

        LOGGER.debug("onClose() Received close inventory for player={}", e.getPlayer().getName());

        if (!(e.getPlayer() instanceof Player)){

            LOGGER.debug("onClose() Received HumanEntity(getPlayer())={} is not instance of Player",
                    e.getPlayer().getName());
            return;
        }

        final Player player = (Player) e.getPlayer();

        final Optional<Category> optionalCategory = this.plugin.getCategories().getByInventory(e.getInventory());

        if (!optionalCategory.isPresent()){

            LOGGER.debug("onClose() Category for closed inventory not found. player={}",  player.getName());
            return;
        }

        final Category category = optionalCategory.get();

        LOGGER.debug("onClose() Closing config inventory for category={} and player={}", category.getName(),  player.getName());

        final Inventory inventory = category.getConfigInventory();

        if (inventory.getViewers().size() == 1){
            for(int j = 0; j < 28; j += 27){
                for(int i = j; i < 9; i++){

                    final ItemStack item1 = inventory.getItem(i),
                            item2 = inventory.getItem(i + 9),
                            item3 = inventory.getItem(i + 18);

                    if ((!this.isAirOrNull(item1) || !this.isAirOrNull(item2) || !this.isAirOrNull(item3)) && (this.isAirOrNull(item1) || this.isAirOrNull(item3))){

                        LOGGER.debug("onClose() item1={}, item2={} or item{} is null or air", item1.toString(),
                                item2.toString(), item3.toString());

                        final ItemStack air = new ItemStack(Material.AIR);

                        inventory.setItem(i, air);
                        inventory.setItem(i + 9, air);
                        inventory.setItem(i + 18, air);

                    }
                }
            }
        }

        this.plugin.getCategoryData().save(category);

        LOGGER.debug("onClose() Closed config inventory for category={}, closed by player={}",
                category.getName(), player.getName());
    }

    private boolean isAirOrNull(final ItemStack itemStack){
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
