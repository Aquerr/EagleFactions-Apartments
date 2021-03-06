package io.github.aquerr.efapartments.listener;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.PluginPermissions;
import io.github.aquerr.efapartments.model.SelectionPoints;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class WandUsageListener extends AbstractListener
{
    public WandUsageListener(final EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Listener
    public void onRightClick(final InteractBlockEvent.Secondary event, final @Root Player player)
    {
        if (!player.hasPermission(PluginPermissions.WAND_COMMAND))
            return;

        if(event.getHandType() == HandTypes.MAIN_HAND)
            return;

        if(event.getTargetBlock() == BlockSnapshot.NONE)
            return;

        if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent())
            return;

        final ItemStack itemInHand = player.getItemInHand(HandTypes.MAIN_HAND).get();

        if(!itemInHand.get(Keys.DISPLAY_NAME).isPresent() || !player.getItemInHand(HandTypes.MAIN_HAND).get().get(Keys.DISPLAY_NAME).get().toPlain().equals("EF-A Wand"))
            return;

        SelectionPoints selectionPoints = super.getPlugin().getPlayerSelectionPoints().get(player.getUniqueId());
        if (selectionPoints == null)
        {
            selectionPoints = new SelectionPoints(null, event.getTargetBlock().getPosition());
        }
        else
        {
            selectionPoints.setSecondPoint(event.getTargetBlock().getPosition());
        }

        super.getPlugin().getPlayerSelectionPoints().put(player.getUniqueId(), selectionPoints);
        player.sendMessage(Text.of(TextColors.GOLD, "Second point", TextColors.BLUE, " has been selected at ", TextColors.GOLD, event.getTargetBlock().getPosition()));
        event.setCancelled(true);
    }

    @Listener
    public void onLeftClick(final InteractBlockEvent.Primary event, final @Root Player player)
    {
        if (!player.hasPermission(PluginPermissions.WAND_COMMAND))
            return;

        if(event.getHandType() == HandTypes.OFF_HAND)
            return;

        if(event.getTargetBlock() == BlockSnapshot.NONE)
            return;

        if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent())
            return;

        final ItemStack itemInHand = player.getItemInHand(HandTypes.MAIN_HAND).get();

        if(!itemInHand.get(Keys.DISPLAY_NAME).isPresent() || !player.getItemInHand(HandTypes.MAIN_HAND).get().get(Keys.DISPLAY_NAME).get().toPlain().equals("EF-A Wand"))
            return;

        SelectionPoints selectionPoints = super.getPlugin().getPlayerSelectionPoints().get(player.getUniqueId());
        if (selectionPoints == null)
        {
            selectionPoints = new SelectionPoints(event.getTargetBlock().getPosition(), null);
        }
        else
        {
            selectionPoints.setFirstPoint(event.getTargetBlock().getPosition());
        }

        super.getPlugin().getPlayerSelectionPoints().put(player.getUniqueId(), selectionPoints);
        player.sendMessage(Text.of(TextColors.GOLD, "First point", TextColors.BLUE, " has been selected at ", TextColors.GOLD, event.getTargetBlock().getPosition()));
        event.setCancelled(true);
    }
}
