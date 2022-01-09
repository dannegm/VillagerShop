package net.dajman.villagershop.inventory.listeners.actionservice.config.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class ConfigInventoryChangingPageCache {

    private static final Logger LOGGER = Logger.getLogger(ConfigInventoryChangingPageCache.class);

    private final LoadingCache<Player, Boolean> cache;

    public ConfigInventoryChangingPageCache() {

        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build(new CacheLoader<Player, Boolean>() {
            @Override
            public Boolean load(final Player player){
                return Boolean.FALSE;
            }
        });

    }

    public Boolean isCached(final Player player){

        if (isNull(player)){
            LOGGER.warn("isCached() Trying to check changing cache for null player");
            return Boolean.FALSE;
        }

        try {

            return this.cache.get(player);

        } catch (final ExecutionException e) {

            LOGGER.error("isCached() Error while checking changing page cache for player={}. {}",
                    player.getName(), e);
        }

        return Boolean.FALSE;
    }

    public void cachePlayer(final Player player){

        if (isNull(player)){
            LOGGER.warn("cachePlayer() Trying to cache null player.");
            return;
        }

        this.cache.put(player, Boolean.TRUE);
    }

    public void invalidate(final Player player){

        if (isNull(player)){
            LOGGER.warn("cachePlayer() Trying to invalidate cache of null player.");
            return;
        }

        this.cache.invalidate(player);
    }
}
