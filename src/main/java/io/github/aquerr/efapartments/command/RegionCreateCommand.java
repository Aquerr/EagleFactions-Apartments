package io.github.aquerr.efapartments.command;

import com.flowpowered.math.vector.Vector3i;
import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import io.github.aquerr.efapartments.model.SelectionPoints;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;

import java.util.Optional;

public class RegionCreateCommand extends AbstractCommand
{
    public RegionCreateCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Only in-game players can use this command!")));

        final Player player = (Player)source;
        final String name = args.requireOne(Text.of("name"));
        final double pricePerDay = args.requireOne(Text.of("price_per_day"));

        final Optional<Faction> optionalFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByPlayerUUID(player.getUniqueId());
        if (!optionalFaction.isPresent() || !optionalFaction.get().getLeader().equals(player.getUniqueId()))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must be the faction's leader to use this command!")));

        final Faction playerFaction = optionalFaction.get();
        if (super.getPlugin().getRegionManager().find(playerFaction.getName(), name) != null)
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Region with given name already exists!")));

        // Check if new region boundaries exists inside the faction's territory.
        final SelectionPoints selectionPoints = super.getPlugin().getPlayerSelectionPoints().get(player.getUniqueId());
        if (selectionPoints == null || selectionPoints.getFirstPoint() == null || selectionPoints.getSecondPoint() == null)
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("You must select two corners before creating a region!")));

        final Vector3i firstPoint = selectionPoints.getFirstPoint();
        final Vector3i secondPoint = selectionPoints.getSecondPoint();

        final Optional<Faction> optionalFactionAtFirstPoint = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), player.getWorld().getChunkAtBlock(firstPoint).get().getPosition());
        final Optional<Faction> optionalFactionAtSecondPoint = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), player.getWorld().getChunkAtBlock(secondPoint).get().getPosition());

        if ((optionalFactionAtFirstPoint.isPresent() && optionalFactionAtFirstPoint.get().getName().equals(playerFaction.getName()))
                && (optionalFactionAtSecondPoint.isPresent() && optionalFactionAtSecondPoint.get().getName().equals(playerFaction.getName())))
        {
            final Region region = new Region(super.getPlugin().getRegionManager().getNewFreeId(), name, playerFaction.getName(), new AABB(firstPoint, secondPoint), (float)pricePerDay);
            super.getPlugin().getRegionManager().save(region);
            player.sendMessage(EagleFactionsApartments.PLUGIN_PREFIX.concat(Text.of("Region has been successfully created!")));
        }
        else
        {
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Selected corners must be inside your faction!")));
        }

        return CommandResult.success();
    }
}
