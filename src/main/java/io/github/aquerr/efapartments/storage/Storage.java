package io.github.aquerr.efapartments.storage;

import io.github.aquerr.efapartments.model.Region;

import java.util.List;

public interface Storage
{
    int getNewId();

    void reload();

    Region findRegion(String factionName, int id);
    Region findRegion(String factionName, String name);

    List<Region> findAll();
    List<Region> findAllForFaction(String factionName);

    void save(Region object);

    void delete(Region object);
    void delete(int id);

    void deleteAllForFaction(String factionName);
}
