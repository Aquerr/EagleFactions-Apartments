package io.github.aquerr.efapartments.listener;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.List;
import java.util.Optional;

public class BlockChangeListener extends AbstractListener
{
    public BlockChangeListener(final EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Listener(order = Order.LAST)
    public void onBlockBreak(final ChangeBlockEvent.Break event, final @Root Player player)
    {
        final Optional<Faction> optionalChunkFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), player.getLocation().getChunkPosition());
        if (!optionalChunkFaction.isPresent())
            return;

        final Faction faction = optionalChunkFaction.get();
        final List<Region> factionRegions = super.getPlugin().getRegionManager().findAllForFaction(faction.getName());
        for (final Region region : factionRegions)
        {
            if (region.getAabb().contains(player.getPosition()))
            {
                if (!region.getRentBy().equals(player.getUniqueId()))
                {
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onBlockPlace(final ChangeBlockEvent.Place event, final @Root Player player)
    {
        final Optional<Faction> optionalChunkFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), player.getLocation().getChunkPosition());
        if (!optionalChunkFaction.isPresent())
            return;

        final Faction faction = optionalChunkFaction.get();
        final List<Region> factionRegions = super.getPlugin().getRegionManager().findAllForFaction(faction.getName());
        for (final Region region : factionRegions)
        {
            if (region.getAabb().contains(player.getPosition()))
            {
                if (!region.getRentBy().equals(player.getUniqueId()))
                {
                    event.setCancelled(true);
                }
                break;
            }
        }
    }
}
