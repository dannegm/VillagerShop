package net.dajman.villagershop.util.serializer.itemstack;

import net.dajman.villagershop.util.logging.Logger;
import net.dajman.villagershop.util.serializer.Serializer;
import net.dajman.villagershop.util.serializer.itemstack.meta.potion.PotionMetaSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.io.*;
import java.util.*;

import static net.dajman.villagershop.util.logging.Logger.getMessage;


public class ItemStackSerializer implements Serializer<ItemStack[], String> {

    private static final Logger LOGGER = Logger.getLogger(ItemStackSerializer.class);

    private static final String ITEM_META_KEY = "meta";

    private final PotionMetaSerializer potionMetaSerializer;

    public ItemStackSerializer(){
        this.potionMetaSerializer = new PotionMetaSerializer();
    }

    @Override
    public Optional<String> serialize(ItemStack[] items) {

        final Map[] serializedItems = Arrays.stream(items)
                .map(itemStack -> {

                    itemStack = Optional.ofNullable(itemStack).orElse(new ItemStack(Material.AIR));

                    final Map<String, Object> serializedItem = new HashMap<>(itemStack.serialize());

                    if (itemStack.hasItemMeta()){

                        ItemMeta itemMeta = itemStack.getItemMeta();

                        Optional<Map<String, Object>> serializedItemMeta;

                        if (itemMeta instanceof PotionMeta){
                            serializedItemMeta = this.potionMetaSerializer.serialize((PotionMeta) itemMeta);
                        } else {
                            serializedItemMeta = Optional.of(new HashMap<>(itemMeta.serialize()));
                        }


                        // validate serialized meta
                        if (serializedItemMeta.isPresent()){

                            try{
                                new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(serializedItemMeta.get());

                            } catch (IOException e) {

                                LOGGER.error("serialize() error while serialization itemMeta={} of itemStack={}. {}",
                                        itemMeta.toString(), itemStack.toString(), getMessage(e));

                                serializedItemMeta = Optional.empty();

                                if (LOGGER.isDebugMode()){
                                    e.printStackTrace();
                                }
                            }
                        }

                        serializedItem.put(ITEM_META_KEY, serializedItemMeta.orElse(Collections.emptyMap()));
                    }

                    LOGGER.debug("serialize() itemstack={} serialized={}", itemStack.toString(),
                            serializedItem.toString());

                    return serializedItem;
                })
                .toArray(Map[]::new);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try{
            final ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(serializedItems);
            oos.flush();

            return Optional.of(Base64.getEncoder().encodeToString(bos.toByteArray()));

        }catch (IOException e){

            if (e instanceof NotSerializableException){
                LOGGER.error("serialize() error while serialization items={}. {}",
                        Arrays.toString(items), getMessage(e));
            } else {
                LOGGER.error("serialize() Error while writing items={} to string", Arrays.toString(items),
                        getMessage(e));
            }

            if (LOGGER.isDebugMode()){
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ItemStack[]> deserialize(String encodedItems) {
        final byte[] decodedItems = Base64.getDecoder().decode(encodedItems);

        final ByteArrayInputStream bis = new ByteArrayInputStream(decodedItems);

        try{

            final ObjectInputStream ois = new ObjectInputStream(bis);

            final Map<String, Object>[] serializedItems = (Map<String, Object>[]) ois.readObject();

            return Optional.of(Arrays.stream(serializedItems).map(serializedItem -> {

                Optional<ItemMeta> deserializedItemMeta = Optional.empty();

                if (serializedItem.containsKey(ITEM_META_KEY)){

                    final Map<String, Object> serializedMeta = new HashMap<>((Map<String, Object>) serializedItem.remove("meta"));
                    serializedMeta.put("==", "ItemMeta");

                    if ("POTION".equals(serializedMeta.get("meta-type"))){
                        deserializedItemMeta = Optional.ofNullable(this.potionMetaSerializer.deserialize(serializedMeta)
                                .orElse(null));
                    } else {
                        deserializedItemMeta = Optional.ofNullable((ItemMeta) ConfigurationSerialization.deserializeObject(serializedMeta));
                    }
                }

                final ItemStack deserializedItem = ItemStack.deserialize(serializedItem);
                deserializedItemMeta.ifPresent(deserializedItem::setItemMeta);

                return deserializedItem;

            }).toArray(ItemStack[]::new));

        }catch (IOException | ClassNotFoundException e){

            if (LOGGER.debug("deserialize() Error while reading encodedItems={}", encodedItems)){
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }
}