package io.github.aquerr.efapartments.manager;

import io.github.aquerr.efapartments.model.Region;
import io.github.aquerr.efapartments.storage.RegionStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegionRegionManagerImpl implements RegionManager
{
//    private final Queue<StorageTask> storageTaskQueue = new ConcurrentLinkedDeque<>();

    private final RegionStorage regionStorage;

    public RegionRegionManagerImpl(final RegionStorage regionStorage)
    {
        this.regionStorage = regionStorage;
    }

    @Override
    public int getNewFreeId()
    {
        return this.regionStorage.getNewId();
    }

    @Override
    public Region find(final String factionName, final int id)
    {
        final CompletableFuture<Region> future = CompletableFuture.supplyAsync(() -> regionStorage.findRegion(factionName, id));
        return future.join();
    }

    @Override
    public Region find(String factionName, String name)
    {
        final CompletableFuture<Region> future = CompletableFuture.supplyAsync(() -> regionStorage.findRegion(factionName, name));
        return future.join();
    }



    @Override
    public List<Region> findAll()
    {
        return new ArrayList<>(regionStorage.findAll());
    }

    @Override
    public List<Region> findAllForFaction(String factionName)
    {
        final CompletableFuture<List<Region>> future = CompletableFuture.supplyAsync(() -> regionStorage.findAllForFaction(factionName));
        return new ArrayList<>(future.join());
    }

    @Override
    public void save(Region region)
    {
        regionStorage.save(region);
    }

    @Override
    public void delete(Region region)
    {
        regionStorage.delete(region);
    }

    @Override
    public void delete(int id)
    {
        regionStorage.delete(id);
    }

    @Override
    public void deleteAllForFaction(String factionName)
    {
        this.regionStorage.deleteAllForFaction(factionName);
    }
}
