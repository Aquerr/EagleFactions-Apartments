package io.github.aquerr.efapartments.manager;

import io.github.aquerr.efapartments.model.Region;

import java.util.List;

public interface RegionManager
{
    int getNewFreeId();

    Region find(String factionName, int id);
    Region find(String factionName, String name);

    List<Region> findAll();
    List<Region> findAllForFaction(String factionName);

    void save(Region value);

    void delete(Region value);
    void delete(int id);

    void deleteAllForFaction(String factionName);
}
