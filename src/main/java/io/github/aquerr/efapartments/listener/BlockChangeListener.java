package io.github.aquerr.efapartments.listener;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
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
        final List<Transaction<BlockSnapshot>> blocks = event.getTransactions();
        for (final Transaction<BlockSnapshot> transaction : blocks)
        {
            final Optional<Faction> optionalChunkFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(transaction.getOriginal().getWorldUniqueId(), transaction.getOriginal().getLocation().get().getChunkPosition());
            if (!optionalChunkFaction.isPresent())
                continue;

            final Faction faction = optionalChunkFaction.get();
            final List<Region> factionRegions = super.getPlugin().getRegionManager().findAllForFaction(faction.getName());

            for (final Region region : factionRegions)
            {
                if (region.getAabb().contains(transaction.getOriginal().getPosition()))
                {
//                    if (!player.getUniqueId().equals(region.getRentBy()))
//                    {
                        event.setCancelled(true);
//                    }
                    break;
                }
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onBlockPlace(final ChangeBlockEvent.Place event, final @Root Player player)
    {
        final List<Transaction<BlockSnapshot>> blocks = event.getTransactions();
        for (final Transaction<BlockSnapshot> transaction : blocks)
        {
            final Optional<Faction> optionalChunkFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(transaction.getOriginal().getWorldUniqueId(), transaction.getOriginal().getLocation().get().getChunkPosition());
            if (!optionalChunkFaction.isPresent())
                continue;

            final Faction faction = optionalChunkFaction.get();
            final List<Region> factionRegions = super.getPlugin().getRegionManager().findAllForFaction(faction.getName());
            for (final Region region : factionRegions)
            {
                if (region.getAabb().contains(transaction.getOriginal().getPosition()))
                {
//                    if (!player.getUniqueId().equals(region.getRentBy()))
//                    {
                        event.setCancelled(true);
//                    }
                    break;
                }
            }
        }
    }
}
