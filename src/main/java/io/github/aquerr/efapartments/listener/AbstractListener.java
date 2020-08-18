package io.github.aquerr.efapartments.listener;

import io.github.aquerr.efapartments.EagleFactionsApartments;

public abstract class AbstractListener
{
    private final EagleFactionsApartments plugin;

    protected AbstractListener(final EagleFactionsApartments plugin)
    {
        this.plugin = plugin;
    }

    public EagleFactionsApartments getPlugin()
    {
        return plugin;
    }
}
