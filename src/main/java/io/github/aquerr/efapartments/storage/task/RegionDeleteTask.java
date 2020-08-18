package io.github.aquerr.efapartments.storage.task;

public class RegionDeleteTask
{
    private final String factionName;

    public RegionDeleteTask(final String factionName)
    {
        this.factionName = factionName;
    }

    public String getFactionName()
    {
        return factionName;
    }
}
