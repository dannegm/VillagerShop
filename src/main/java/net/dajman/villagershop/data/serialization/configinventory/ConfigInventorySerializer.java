package net.dajman.villagershop.data.serialization.configinventory;

import net.dajman.villagershop.common.Serializer;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.common.Items;
import net.dajman.villagershop.inventory.common.Strings;
import net.dajman.villagershop.data.serialization.itemstack.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class ConfigInventorySerializer implements Serializer<List<Inventory>, String> {

    public static final ConfigInventorySerializer INSTANCE = new ConfigInventorySerializer();
    
    private static final Logger LOGGER = Logger.getLogger(ConfigInventorySerializer.class);

    private final ItemStackSerializer itemStackSerializer;

    public ConfigInventorySerializer() {
        this.itemStackSerializer = new ItemStackSerializer();
    }

    @Override
    public Optional<String> serialize(List<Inventory> inventories) {
        
        LOGGER.debug("serialize() Attempted serialization inventories {}", inventories);

        if (isNull(inventories) || inventories.isEmpty()){

            LOGGER.warn("serialize() Given inventories list {} is null or empty", inventories);

            return Optional.of("");
        }

        final List<ItemStack> itemStacks = new ArrayList<>();

        for(Inventory inventory : inventories){

            LOGGER.debug("serialize() Trying to serialize inventory {}", inventory);

            final ItemStack[] invContents = inventory.getContents().clone();

            if (!Objects.equals(invContents.length, 36)){

                LOGGER.error("serialize() Inventory {} has wrong size, skipping serialization",
                        inventory);
                continue;
            }

            itemStacks.addAll(Arrays.stream(Arrays.copyOf(invContents, 27)).collect(toList()));
        }

        final ItemStack[] contentsToSerialize = itemStacks.toArray(new ItemStack[0]);

        LOGGER.debug("serialize() Serialization of itemStacks {}",
                Arrays.toString(contentsToSerialize));

        return this.itemStackSerializer.serialize(contentsToSerialize);
    }

    @Override
    public Optional<List<Inventory>> deserialize(String serialized) {

        LOGGER.debug("deserialize() Trying to deserialize inventories. {}", serialized);

        if (isNull(serialized) || serialized.isEmpty()){
            LOGGER.debug("deserialize() Given string is null or empty");

            return Optional.empty();
        }

        final Optional<ItemStack[]> optionalDeserializedItemStacks = this.itemStackSerializer.deserialize(serialized);

        if (!optionalDeserializedItemStacks.isPresent()){

            LOGGER.debug("deserialize() Optional of deserialized itemStacks is empty");

            return Optional.empty();
        }

        final ItemStack[] deserializedItemStacks = optionalDeserializedItemStacks.get();

        int numberOfInventories = deserializedItemStacks.length / 27;

        if (!Objects.equals(deserializedItemStacks.length % 27, 0)){

            LOGGER.warn("deserialize() Incorrect length of deserialized items ({}). " +
                    "Should be a multiple of 27", deserializedItemStacks.length);

            numberOfInventories++;
        }

        final List<Inventory> inventories = new ArrayList<>();

        for(int inventoryNumber = 0; inventoryNumber < numberOfInventories; inventoryNumber++){

            final int startIndex = inventoryNumber * 27;

            final int endIndex = Math.min(deserializedItemStacks.length, (inventoryNumber + 1) * 27);

            final List<ItemStack> contentsList = Arrays.stream(Arrays.copyOfRange(deserializedItemStacks, startIndex, endIndex))
                    .collect(toList());

            for(int slot = contentsList.size(); slot < 36; slot++){
                contentsList.add(new ItemStack(Material.AIR));
            }

            contentsList.set(27, Items.CONFIG_INVENTORY_PREVIOUS_PAGE_ITEM_STACK());
            contentsList.set(35, Items.CONFIG_INVENTORY_NEXT_PAGE_ITEM_STACK());

            final ItemStack grayStainedGlassPane = Items.GRAY_STAINED_GLASS_PANE_ITEM_STACK();

            for(int i = 28; i < 35; i++){
                contentsList.set(i, grayStainedGlassPane);
            }


            final ItemStack[] contents = contentsList.toArray(new ItemStack[0]);

            final Inventory inventory = Bukkit.createInventory(null, 36,
                    Strings.CONFIG_INVENTORY_TITLE_PREFIX() + Integer.toString(inventoryNumber + 1));

            inventory.setContents(contents);

            inventories.add(inventory);
        }

        LOGGER.debug("deserialize() Created {} inventories.", inventories.size());

        return Optional.of(inventories);
    }
}
