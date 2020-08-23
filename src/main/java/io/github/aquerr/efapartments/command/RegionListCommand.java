package io.github.aquerr.efapartments.command;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RegionListCommand extends AbstractCommand
{
    public RegionListCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        if (!(source instanceof Player))
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Only in-game players can use this command!")));

        final Player player = (Player)source;

        final Optional<Faction> optionalFaction = super.getPlugin().getEagleFactions().getFactionLogic().getFactionByPlayerUUID(player.getUniqueId());
        if (!optionalFaction.isPresent())
            throw new CommandException(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of(TextColors.RED, "You must be in faction in order to use this command!")));

        final List<Region> regions = super.getPlugin().getRegionManager().findAllForFaction(optionalFaction.get().getName());
        final List<Text> regionsLines = new LinkedList<>();

        for (final Region region : regions)
        {
            regionsLines.add(Text.of("- " + region.getName()));
        }

        final PaginationList paginationList = PaginationList.builder().contents(regionsLines).header(Text.of(TextColors.AQUA, "Faction Regions")).padding(Text.of(TextColors.AQUA, "-")).linesPerPage(14).build();
        paginationList.sendTo(player);

        return CommandResult.success();
    }
}
