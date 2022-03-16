package net.dajman.villagershop.data.serialization.itemstack;

import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.common.Serializer;
import net.dajman.villagershop.data.serialization.itemstack.meta.ItemMetaSerializer;
import net.dajman.villagershop.data.serialization.itemstack.meta.potion.PotionMetaSerializer;
import net.dajman.villagershop.data.serialization.itemstack.meta.skull.SkullMetaSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.*;
import java.util.*;

import static java.util.Objects.isNull;


public class ItemStackSerializer implements Serializer<ItemStack[], String> {

    private static final Logger LOGGER = Logger.getLogger(ItemStackSerializer.class);

    private static final String ITEM_META_KEY = "meta";

    private final ItemMetaSerializer itemMetaSerializer;

    public ItemStackSerializer(){
        this.itemMetaSerializer = new ItemMetaSerializer();
    }

    @Override
    public Optional<String> serialize(ItemStack[] items) {

        final Map[] serializedItems = Arrays.stream(items)
                .map(itemStack -> {

                    if (isNull(itemStack)){
                        return Collections.emptyMap();
                    }

                    final Map<String, Object> serializedItem = new HashMap<>(itemStack.serialize());

                    if (itemStack.hasItemMeta()){

                        final ItemMeta itemMeta = itemStack.getItemMeta();

                        Optional<Map<String, Object>> serializedItemMeta = this.itemMetaSerializer.serialize(itemMeta);

                        // validate serialized meta
                        if (serializedItemMeta.isPresent()){

                            try{
                                new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(serializedItemMeta.get());

                            } catch (final IOException e) {

                                LOGGER.error("serialize() error while serialization itemMeta={} of itemStack={}. {}",
                                        itemMeta.toString(), itemStack.toString(), e);

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

        }catch (final IOException e){

            if (e instanceof NotSerializableException){
                LOGGER.error("serialize() error while serialization items={}. {}",
                        Arrays.toString(items), e);
            } else {
                LOGGER.error("serialize() Error while writing items={} to string", Arrays.toString(items), e);
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

                if (serializedItem.isEmpty()){
                    return new ItemStack(Material.AIR);
                }

                Optional<ItemMeta> deserializedItemMeta = Optional.empty();

                if (serializedItem.containsKey(ITEM_META_KEY)){

                    final Map<String, Object> serializedMeta = new HashMap<>((Map<String, Object>) serializedItem.remove("meta"));
                    serializedMeta.put("==", "ItemMeta");

                    deserializedItemMeta = this.itemMetaSerializer.deserialize(serializedMeta);
                }

                final ItemStack deserializedItem = ItemStack.deserialize(serializedItem);
                deserializedItemMeta.ifPresent(deserializedItem::setItemMeta);

                LOGGER.debug("deserialize() deserialized item={} from serialized={}",
                        deserializedItem.toString(), serializedItem.toString());

                return deserializedItem;

            }).toArray(ItemStack[]::new));

        }catch (final IOException | ClassNotFoundException e){

            if (LOGGER.debug("deserialize() Error while reading encodedItems={}", encodedItems)){
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }
}