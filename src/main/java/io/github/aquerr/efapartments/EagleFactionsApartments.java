package io.github.aquerr.efapartments;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.aquerr.eaglefactions.api.EagleFactions;
import io.github.aquerr.efapartments.command.*;
import io.github.aquerr.efapartments.command.args.RegionCommandElement;
import io.github.aquerr.efapartments.listener.BlockChangeListener;
import io.github.aquerr.efapartments.listener.WandUsageListener;
import io.github.aquerr.efapartments.manager.RegionRegionManagerImpl;
import io.github.aquerr.efapartments.manager.RegionManager;
import io.github.aquerr.efapartments.model.Region;
import io.github.aquerr.efapartments.model.SelectionPoints;
import io.github.aquerr.efapartments.storage.RegionStorage;
import io.github.aquerr.efapartments.storage.serializer.RegionTypeSerializer;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
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
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private EconomyService economyService;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    public EagleFactionsApartments()
    {

    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event)
    {
        registerTypeSerializers();

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
        // Load Eagle Factions
        Optional<?> optionalEagleFactionsInstance = Sponge.getPluginManager().getPlugin("eaglefactions").get().getInstance();
        optionalEagleFactionsInstance.ifPresent(o -> this.eagleFactions = (EagleFactions) o);

        if (this.eagleFactions == null)
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_ERROR_PREFIX, TextColors.RED, "Could not establish connection with Eagle Factions. This plugin will be stopped!"));
            stopPlugin();
        }
        else
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_PREFIX, TextColors.GREEN, "Successfully connected to Eagle Factions!"));
        }


        // Load economy service
        final Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);
        optionalEconomyService.ifPresent(economyService1 -> this.economyService = economyService1);

        if (this.economyService == null)
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_ERROR_PREFIX, TextColors.RED, "Could not find an economy provider on the server. One is required for this plugin to run!"));
            stopPlugin();
        }
        else
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_PREFIX, TextColors.GREEN, "Successfully connected to an economy provider! We got it boys!"));
            eagleFactions.printInfo("Hello! Nice to see you " + NAME);
        }
    }

    @Listener
    public void onGameStart(final GameStartedServerEvent event)
    {
        startRentExpiryChecker();
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

    public EconomyService getEconomyService()
    {
        return this.economyService;
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

        SUBCOMMANDS.put(Collections.singletonList("rent"), CommandSpec.builder()
                .description(Text.of("Rents a selected region"))
                .permission(PluginPermissions.RENT_COMMAND)
                .executor(new RentCommand(this))
                .arguments(new RegionCommandElement(this, Text.of("region")), GenericArguments.duration(Text.of("duration")))
                .build());

        SUBCOMMANDS.put(Collections.singletonList("deleteregion"), CommandSpec.builder()
                .description(Text.of("Deletes a specific region"))
                .permission(PluginPermissions.REGION_DELETE_COMMAND)
                .executor(new RegionDeleteCommand(this))
                .arguments(new RegionCommandElement(this, Text.of("region")))
                .build());

        CommandSpec eagleFactionsApartmentsRootCommand = CommandSpec.builder()
                .children(SUBCOMMANDS)
                .build();

        Sponge.getCommandManager().register(this, eagleFactionsApartmentsRootCommand, "fa", "efa", "factionsapartments", "eaglefactionsapartments");
    }

    private void registerListeners()
    {
        final EventManager eventManager = Sponge.getEventManager();
        eventManager.registerListeners(this, new BlockChangeListener(this));
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

        Sponge.getServer().getConsole().sendMessage(Text.of(PLUGIN_ERROR_PREFIX, TextColors.RED, NAME + " turned off."));
    }

    private void registerTypeSerializers()
    {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Region.class), new RegionTypeSerializer());
    }

    private void startRentExpiryChecker()
    {
        final Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        //TODO: Let server owner change interval in config file.
        taskBuilder.execute(new RentExpiryCheckTask(this)).interval(10, TimeUnit.MINUTES).async().submit(this);
    }
}
