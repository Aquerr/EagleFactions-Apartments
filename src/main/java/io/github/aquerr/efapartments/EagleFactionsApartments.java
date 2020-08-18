package io.github.aquerr.efapartments;

import com.google.inject.Inject;
import io.github.aquerr.eaglefactions.api.EagleFactions;
import io.github.aquerr.efapartments.command.RegionCreateCommand;
import io.github.aquerr.efapartments.command.RegionListCommand;
import io.github.aquerr.efapartments.command.WandCommand;
import io.github.aquerr.efapartments.listener.BlockBreakListener;
import io.github.aquerr.efapartments.listener.WandUsageListener;
import io.github.aquerr.efapartments.manager.RegionRegionManagerImpl;
import io.github.aquerr.efapartments.manager.RegionManager;
import io.github.aquerr.efapartments.model.Region;
import io.github.aquerr.efapartments.model.SelectionPoints;
import io.github.aquerr.efapartments.storage.RegionStorage;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = EagleFactionsApartments.ID,
        name = EagleFactionsApartments.NAME,
        description = EagleFactionsApartments.DESCRIPTION,
        url = EagleFactionsApartments.URL,
        authors = EagleFactionsApartments.AUTHORS,
        dependencies = {
                @Dependency(id = "eaglefactions")
        }
)
public class EagleFactionsApartments
{
    public static final String ID = "eaglefactions-apartments";
    public static final String NAME = "Eaglefactions Apartments";
    public static final String DESCRIPTION = "An add-on for Eagle Factions that lets leaders rent specific regions for factions members.";
    public static final String URL = "https://github.com/Aquerr/EagleFactions-Apartments";
    public static final String AUTHORS = "Aquerr";

    public static final Text PLUGIN_ERROR_PREFIX = Text.of(TextColors.RED, "[EF-A] ");
    public static final Text PLUGIN_PREFIX = Text.of(TextColors.AQUA, "[EF-A] ");

    public static final Map<List<String>, CommandCallable> SUBCOMMANDS = new HashMap<>();


    private Map<UUID, SelectionPoints> playerSelectionPoints = new HashMap<>();


    private RegionManager regionManager;

    // Integration
    private EagleFactions eagleFactions;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    public EagleFactionsApartments()
    {
        RegionStorage regionStorage = new RegionStorage(this.configDir);
        this.regionManager = new RegionRegionManagerImpl(regionStorage);
    }

    @Listener
    public void onInit(GameInitializationEvent event)
    {
        registerCommands();
        registerListeners();
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event)
    {
        Optional<?> optionalEagleFactionsInstance = Sponge.getPluginManager().getPlugin("eaglefactions").get().getInstance();
        optionalEagleFactionsInstance.ifPresent(o -> this.eagleFactions = (EagleFactions) o);

        if (this.eagleFactions == null)
        {
            //TODO: Stop this add-on plugin. It cannot work without Eagle Factions...
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_ERROR_PREFIX, "Could not establish connection with Eagle Factions. This plugin will be stopped!"));
            stopPlugin();
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_ERROR_PREFIX, NAME + " turned off."));
        }
    }

    public Path getConfigDir()
    {
        return this.configDir;
    }

    public RegionManager getRegionManager()
    {
        return this.regionManager;
    }

    public Map<UUID, SelectionPoints> getPlayerSelectionPoints()
    {
        return this.playerSelectionPoints;
    }

    public EagleFactions getEagleFactions()
    {
        return this.eagleFactions;
    }

    private void registerCommands()
    {
        SUBCOMMANDS.put(Collections.singletonList("wand"), CommandSpec.builder()
                .description(Text.of("Gives EF-Apartments Wand"))
                .permission(PluginPermissions.WAND_COMMAND)
                .executor(new WandCommand(this))
                .build());

        SUBCOMMANDS.put(Collections.singletonList("createregion"), CommandSpec.builder()
                .description(Text.of("Creates a rentable region"))
                .permission(PluginPermissions.REGION_CREATE_COMMAND)
                .arguments(GenericArguments.string(Text.of("name")), GenericArguments.doubleNum(Text.of("price_per_day")))
                .executor(new RegionCreateCommand(this))
                .build());

        SUBCOMMANDS.put(Collections.singletonList("listregions"), CommandSpec.builder()
                .description(Text.of("List all rentable regions for your faction"))
                .permission(PluginPermissions.REGION_LIST_COMMAND)
                .executor(new RegionListCommand(this))
                .build());

        CommandSpec eagleFactionsApartmentsRootCommand = CommandSpec.builder()
                .children(SUBCOMMANDS)
                .build();

        Sponge.getCommandManager().register(this, eagleFactionsApartmentsRootCommand, "fa", "efa", "factionsapartments", "eaglefactionsapartments");
    }

    private void registerListeners()
    {
        final EventManager eventManager = Sponge.getEventManager();
        eventManager.registerListeners(this, new BlockBreakListener(this));
        eventManager.registerListeners(this, new WandUsageListener(this));
    }

    private void stopPlugin()
    {
        final EventManager eventManager = Sponge.getEventManager();
        final CommandManager commandManager = Sponge.getCommandManager();

        this.configDir = null;
        this.logger = null;
        this.playerSelectionPoints = null;
        this.regionManager = null;

        eventManager.unregisterPluginListeners(this);
        commandManager.getOwnedBy(this).forEach(commandManager::removeMapping);
        SUBCOMMANDS.clear();
    }
}
