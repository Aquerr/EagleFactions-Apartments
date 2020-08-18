package io.github.aquerr.efapartments.storage;

import com.google.common.reflect.TypeToken;
import io.github.aquerr.efapartments.model.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RegionStorage implements Storage
{
    public static final TypeToken<Region> REGION_TYPE_TOKEN = TypeToken.of(Region.class);

    final Path regionsDirPath;


    public RegionStorage(final Path configDir)
    {
        this.regionsDirPath = configDir.resolve("regions");

        if (Files.notExists(this.regionsDirPath))
        {
            try
            {
                Files.createDirectories(this.regionsDirPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getNewId()
    {
        return 0;
    }

    @Override
    public void reload()
    {

    }

    @Override
    public Region findRegion(String factionName, int id)
    {
        return null;
    }

    @Override
    public Region findRegion(String factionName, String name)
    {
        if (Files.notExists(this.regionsDirPath.resolve(factionName)))
        {
            try
            {
                Files.createDirectory(this.regionsDirPath.resolve(factionName));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // Maybe we should cache loaders instead of creating them everytime?
        // Caching them would open file streams which
        final HoconConfigurationLoader hoconConfigurationLoader = HoconConfigurationLoader.builder().setPath(this.regionsDirPath.resolve(factionName).resolve(name + ".conf")).build();
        try
        {
            final ConfigurationNode configurationNode = hoconConfigurationLoader.load();
            final Region region = configurationNode.getValue(RegionStorage.REGION_TYPE_TOKEN);
            return region;
        }
        catch (IOException | ObjectMappingException e)
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.RED, "Could not deserialize region. Region name = " + name));
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Region> findAll()
    {
        final List<Region> regions = new ArrayList<>();
        final File[] factionDirectories = this.regionsDirPath.toFile().listFiles();
        if (factionDirectories == null)
            return regions;

        for (final File factionDirectory : factionDirectories)
        {
            final List<Region> factionRegions = findAllForFaction(factionDirectory.getName());
            regions.addAll(factionRegions);
        }
        return regions;
    }

    @Override
    public List<Region> findAllForFaction(String factionName)
    {
        final List<Region> regions = new ArrayList<>();
        final File[] regionFiles = this.regionsDirPath.resolve(factionName).toFile().listFiles();
        if (regionFiles == null)
            return regions;

        for (final File regionFile : regionFiles)
        {
            final Region region = findRegion(factionName, regionFile.getName());
            if (region != null)
                regions.add(region);
        }
        return regions;
    }


    @Override
    public void save(Region region)
    {
        final HoconConfigurationLoader hoconConfigurationLoader = HoconConfigurationLoader.builder().setPath(this.regionsDirPath.resolve(region.getFactionName()).resolve(region.getName() + ".conf")).build();
        try
        {
            final ConfigurationNode configurationNode = hoconConfigurationLoader.load();
            configurationNode.setValue(RegionStorage.REGION_TYPE_TOKEN, region);
        }
        catch (IOException | ObjectMappingException e)
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.RED, "Could not serialize region. Region name = " + region.getName()));
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Region region)
    {
        try
        {
            Files.deleteIfExists(this.regionsDirPath.resolve(region.getFactionName()).resolve(region.getName() + ".conf"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id)
    {

    }

    @Override
    public void deleteAllForFaction(String factionName)
    {
        final File factionDir = this.regionsDirPath.resolve(factionName).toFile();
        if (!factionDir.exists())
            return;

        final File[] regionFiles = factionDir.listFiles();
        if (regionFiles == null)
            return;

        for (final File file : regionFiles)
            file.delete();
        factionDir.delete();
    }
}
