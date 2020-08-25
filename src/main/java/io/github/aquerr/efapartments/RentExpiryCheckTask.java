package io.github.aquerr.efapartments;

import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RentExpiryCheckTask implements Runnable
{
    private final EagleFactionsApartments plugin;

    public RentExpiryCheckTask(final EagleFactionsApartments plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        final List<Region> regions = plugin.getRegionManager().findAll();
        final Instant now = Instant.now();

        for (final Region region : regions)
        {
            if (region.getRentBy() == null || region.getRentExpirationDateTime() == null)
                continue;

            final UUID playerUUID = region.getRentBy();
            if (now.isAfter(region.getRentExpirationDateTime()))
            {
                region.setRentBy(null);
                region.setRentExpiryDateTime(null);
                this.plugin.getRegionManager().save(region);
                Sponge.getServer().getPlayer(playerUUID).ifPresent(player -> player.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, Text.of("Your region hire has expired."))));
                continue;
            }

            final Duration timeToExpiration = Duration.between(now, region.getRentExpirationDateTime());
            if (timeToExpiration.abs().getSeconds() / 60 < 5)
            {
                Sponge.getServer().getPlayer(playerUUID).ifPresent(player -> player.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, Text.of("Your region rent will expiry in 5 min."))));
                continue;
            }

            if (timeToExpiration.abs().getSeconds() / 60 < 10)
            {
                Sponge.getServer().getPlayer(playerUUID).ifPresent(player -> player.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_PREFIX, Text.of("Your region rent will expiry in 10 min."))));
            }
        }
    }
}
