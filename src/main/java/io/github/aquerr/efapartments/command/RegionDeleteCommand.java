package io.github.aquerr.efapartments.command;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class RegionDeleteCommand extends AbstractCommand
{
    public RegionDeleteCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        final Region region = args.requireOne(Text.of("region"));

        super.getPlugin().getRegionManager().delete(region);
        src.sendMessage(Text.of(EagleFactionsApartments.PLUGIN_ERROR_PREFIX.concat(Text.of("Region has been successfully deleted."))));
        return CommandResult.success();
    }
}
