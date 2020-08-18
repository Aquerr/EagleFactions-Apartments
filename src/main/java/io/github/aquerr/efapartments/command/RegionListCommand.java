package io.github.aquerr.efapartments.command;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

public class RegionListCommand extends AbstractCommand
{
    public RegionListCommand(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException
    {
        return CommandResult.success();
    }
}
