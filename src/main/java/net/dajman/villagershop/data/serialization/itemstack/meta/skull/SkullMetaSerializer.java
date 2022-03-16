package net.dajman.villagershop.data.serialization.itemstack.meta.skull;

import net.dajman.villagershop.common.Serializer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SkullMetaSerializer implements Serializer<SkullMeta, Map<String, Object>> {

    private static final String SKULL_OWNER_KEY = "owner";

    @Override
    public Optional<Map<String, Object>> serialize(SkullMeta skullMeta) {

        final Optional<String> optionalOwner = Optional.ofNullable(skullMeta.getOwner());

        skullMeta.setOwner(null);

        final Map<String, Object> serializedSkullMeta = new HashMap<>(skullMeta.serialize());

        optionalOwner.ifPresent(owner -> serializedSkullMeta.put(SKULL_OWNER_KEY, owner));

        return Optional.of(serializedSkullMeta);
    }

    @Override
    public Optional<SkullMeta> deserialize(Map<String, Object> serializedSkullMeta) {

        final Optional<String> optionalOwner = Optional.ofNullable((String)
                serializedSkullMeta.remove(SKULL_OWNER_KEY));

        final Optional<ItemMeta> optionalItemMeta = Optional.ofNullable((ItemMeta)
                ConfigurationSerialization.deserializeObject(serializedSkullMeta));

        if (optionalItemMeta.isPresent()) {

            final ItemMeta itemMeta = optionalItemMeta.get();

            if (!(itemMeta instanceof SkullMeta)) {
                return Optional.empty();
            }

            final SkullMeta skullMeta = (SkullMeta) itemMeta;

            optionalOwner.ifPresent(skullMeta::setOwner);

            return Optional.of(skullMeta);
        }

        return Optional.empty();
    }
}
