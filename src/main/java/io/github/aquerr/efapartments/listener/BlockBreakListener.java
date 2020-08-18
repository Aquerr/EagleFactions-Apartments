package io.github.aquerr.efapartments.listener;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class BlockBreakListener extends AbstractListener
{
    public BlockBreakListener(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Listener
    public void onBlockBreak(final ChangeBlockEvent.Break event, final @Root Player player)
    {

    }
}
