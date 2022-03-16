package net.dajman.villagershop.data.serialization.itemstack.meta;

import net.dajman.villagershop.common.Serializer;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.data.serialization.itemstack.meta.potion.PotionMetaSerializer;
import net.dajman.villagershop.data.serialization.itemstack.meta.skull.SkullMetaSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemMetaSerializer implements Serializer<ItemMeta, Map<String, Object>> {

    private static final String META_TYPE_KEY = "meta-type";

    private static final String POTION_META_TYPE = "POTION";
    private static final String SKULL_META_TYPE = "SKULL";

    private final PotionMetaSerializer potionMetaSerializer;
    private final SkullMetaSerializer skullMetaSerializer;

    public ItemMetaSerializer() {
        this.potionMetaSerializer = new PotionMetaSerializer();
        this.skullMetaSerializer = new SkullMetaSerializer();
    }

    @Override
    public Optional<Map<String, Object>> serialize(ItemMeta itemMeta) {

        if (itemMeta instanceof PotionMeta) {
            return this.potionMetaSerializer.serialize((PotionMeta) itemMeta);
        }

        if (itemMeta instanceof SkullMeta) {
            return this.skullMetaSerializer.serialize((SkullMeta) itemMeta);
        }

        return Optional.of(new HashMap<>(itemMeta.serialize()));
    }

    @Override
    public Optional<ItemMeta> deserialize(Map<String, Object> serializedItemMeta) {

        final Object metaType = serializedItemMeta.get(META_TYPE_KEY);

        if (POTION_META_TYPE.equals(metaType)) {
            return Optional.ofNullable(this.potionMetaSerializer.deserialize(serializedItemMeta).orElse(null));
        }

        if (SKULL_META_TYPE.equals(metaType)) {
            return Optional.ofNullable(this.skullMetaSerializer.deserialize(serializedItemMeta).orElse(null));
        }

        return Optional.ofNullable((ItemMeta) ConfigurationSerialization.deserializeObject(serializedItemMeta));
    }
}
