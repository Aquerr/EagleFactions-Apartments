package io.github.aquerr.efapartments.listener.faction;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.api.events.FactionRenameEvent;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.listener.AbstractListener;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.event.Listener;

import java.util.List;

public class FactionRenameListener extends AbstractListener
{
    protected FactionRenameListener(EagleFactionsApartments plugin)
    {
        super(plugin);
    }

    @Listener
    public void onFactionRename(final FactionRenameEvent event)
    {
        final Faction faction = event.getFaction();
        final String newName = event.getNewFactionName();

        final List<Region> factionRegions = super.getPlugin().getRegionManager().findAllForFaction(faction.getName());
        super.getPlugin().getRegionManager().deleteAllForFaction(faction.getName());

        for (final Region region : factionRegions)
        {
            super.getPlugin().getRegionManager().save(region.setFactionName(newName));
        }
    }
}
