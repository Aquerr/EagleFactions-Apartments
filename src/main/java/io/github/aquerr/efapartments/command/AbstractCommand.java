package io.github.aquerr.efapartments.command;

import io.github.aquerr.efapartments.EagleFactionsApartments;
import org.spongepowered.api.command.spec.CommandExecutor;

public abstract class AbstractCommand implements CommandExecutor
{
    private final EagleFactionsApartments plugin;

    public AbstractCommand(final EagleFactionsApartments plugin)
    {
        this.plugin = plugin;
    }

    public EagleFactionsApartments getPlugin()
    {
        return this.plugin;
    }
}
