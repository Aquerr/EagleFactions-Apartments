package io.github.aquerr.efapartments.command.args;

import com.google.inject.internal.cglib.core.$LocalVariablesSorter;
import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.efapartments.EagleFactionsApartments;
import io.github.aquerr.efapartments.model.Region;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class RegionCommandElement extends CommandElement
{
    private final EagleFactionsApartments plugin;
    public RegionCommandElement(final EagleFactionsApartments plugin, Text key)
    {
        super(key);
        this.plugin = plugin;
    }

    @Override
    protected Region parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException
    {
        if(!args.hasNext())
            throw args.createError(Text.of("Argument is not a valid region!"));

        if (!(source instanceof Player))
            return null;
        final Player player = (Player)source;
        final Optional<Faction> optionalFaction = this.plugin.getEagleFactions().getFactionLogic().getFactionByPlayerUUID(player.getUniqueId());

        if (!optionalFaction.isPresent())
            return null;

        final Faction faction = optionalFaction.get();
        final String regionName = args.next();

        final Region region = this.plugin.getRegionManager().find(faction.getName(), regionName);
        if(region == null)
            throw args.createError(Text.of("Argument is not a valid region!"));
        return region;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context)
    {
        if (!(src instanceof Player))
            return Collections.emptyList();

        final Player player = (Player) src;
        final Optional<Faction> optionalFaction = this.plugin.getEagleFactions().getFactionLogic().getFactionByPlayerUUID(player.getUniqueId());
        if (!optionalFaction.isPresent())
            return Collections.emptyList();

        final List<Region> regions = this.plugin.getRegionManager().findAllForFaction(optionalFaction.get().getName());
        final List<String> list = regions.stream().map(Region::getName).sorted().collect(Collectors.toList());

        if (args.hasNext())
        {
            String charSequence = args.nextIfPresent().get().toLowerCase();
            return list.stream().filter(x->x.contains(charSequence)).collect(Collectors.toList());
        }

        return list;
    }
}
