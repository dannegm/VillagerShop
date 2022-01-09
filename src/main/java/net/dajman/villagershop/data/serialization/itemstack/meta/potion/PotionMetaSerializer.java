package net.dajman.villagershop.data.serialization.itemstack.meta.potion;

import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.common.Serializer;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class PotionMetaSerializer implements Serializer<PotionMeta, Map<String, Object>> {

    private static final Logger LOGGER = Logger.getLogger(PotionMetaSerializer.class);

    private static final String POTION_CUSTOM_COLOR_KEY = "custom-color";
    private static final String POTION_CUSTOM_EFFECTS_KEY = "custom-effects";

    @Override
    public Optional<Map<String, Object>> serialize(PotionMeta potionMeta) {

        // custom color
        Optional<Object> serializedCustomColor = Optional.empty();

        try{

            final Method getColor = potionMeta.getClass().getDeclaredMethod("getColor");
            getColor.setAccessible(true);

            final Optional<Color> optionalColor = Optional.ofNullable((Color) getColor.invoke(potionMeta));

            if (optionalColor.isPresent()){
                serializedCustomColor = Optional.of(optionalColor.get().asRGB());
            }

            final Method setColor = potionMeta.getClass().getDeclaredMethod("setColor", Color.class);
            setColor.setAccessible(true);

            setColor.invoke(potionMeta, (Object) null);

        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            LOGGER.debug("serialize() Catch exception={} while trying to get customColor from PotionMeta. {}", e);
        }

        // custom effects
        Optional<List<Map<String, Object>>> serializedCustomEffects = Optional.empty();

        if (!potionMeta.getCustomEffects().isEmpty()){

            serializedCustomEffects = Optional.of(potionMeta.getCustomEffects().stream()
                    .map(PotionEffect::serialize).collect(toList()));

            potionMeta.getCustomEffects().forEach(
                    customEffect -> potionMeta.removeCustomEffect(customEffect.getType()));
        }

        final Map<String, Object> serializedItemMeta = new HashMap<>(potionMeta.serialize());

        serializedCustomColor.ifPresent(color -> serializedItemMeta.put(POTION_CUSTOM_COLOR_KEY, color));
        serializedCustomEffects.ifPresent(customEffects -> serializedItemMeta.put(POTION_CUSTOM_EFFECTS_KEY, customEffects));

        return Optional.of(serializedItemMeta);
    }

    @Override
    public Optional<PotionMeta> deserialize(Map<String, Object> serializedPotionMeta) {

        final Optional<Integer> optionalCustomColorRGB = Optional.ofNullable((Integer)
                serializedPotionMeta.remove(POTION_CUSTOM_COLOR_KEY));

        final Optional<List<Map<String, Object>>> optionalCustomColors = Optional.ofNullable(
                (List<Map<String, Object>>) serializedPotionMeta.remove(POTION_CUSTOM_EFFECTS_KEY));

        final Optional<ItemMeta> optionalItemMeta = Optional.ofNullable((ItemMeta)
                ConfigurationSerialization.deserializeObject(serializedPotionMeta));

        if (optionalItemMeta.isPresent()){

            final ItemMeta itemMeta = optionalItemMeta.get();

            if (!(itemMeta instanceof PotionMeta)){
                return Optional.empty();
            }

            final PotionMeta potionMeta = (PotionMeta) itemMeta;

            // deserialize custom color
            optionalCustomColorRGB.ifPresent(customColorRGB -> {
                try {

                    final Method setColor = potionMeta.getClass().getDeclaredMethod("setColor", Color.class);
                    setColor.setAccessible(true);

                    setColor.invoke(potionMeta, Color.fromRGB(customColorRGB));

                } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    LOGGER.debug("deserialize() Erro while trying to set customColor of PotionMeta. {}", e);
                }
            });

            // deserialize custom effects
            optionalCustomColors.ifPresent(customEffects ->
                customEffects.forEach(entry -> potionMeta.addCustomEffect((PotionEffect) ConfigurationSerialization
                        .deserializeObject(entry, PotionEffect.class), true)));

            return Optional.of(potionMeta);
        }
        return Optional.empty();
    }
}
