package io.github.aquerr.efapartments.model;

import org.spongepowered.api.util.AABB;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Region
{
    //TODO: Id may not be needed as we mostly use name and faction name fields.
    private int id;
    private String name;
    private AABB aabb;
    private float pricePerDay;

    //TODO: Add pricePerDay

    // Name of the faction this region belongs to.
    private String factionName;

    // Name of the player that rent this region.
    private UUID rentBy;
    private Instant rentExpiryDateTime;

    public Region()
    {

    }

    public Region(final int id, final String name, final String factionName, final AABB aabb, final float pricePerDay)
    {
        this.id = id;
        this.name = name;
        this.factionName = factionName;
        this.aabb = aabb;
        this.pricePerDay = pricePerDay;
        this.rentBy = null;
        this.rentExpiryDateTime = null;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getFactionName()
    {
        return factionName;
    }

    public AABB getAabb()
    {
        return aabb;
    }

    public float getPricePerDay()
    {
        return pricePerDay;
    }

    public UUID getRentBy()
    {
        return rentBy;
    }

    public Region setId(int id)
    {
        this.id = id;
        return this;
    }

    public Region setName(String name)
    {
        this.name = name;
        return this;
    }

    public Region setAabb(AABB aabb)
    {
        this.aabb = aabb;
        return this;
    }

    public Region setPricePerDay(float pricePerDay)
    {
        this.pricePerDay = pricePerDay;
        return this;
    }

    public Region setRentBy(UUID rentBy)
    {
        this.rentBy = rentBy;
        return this;
    }

    public Region setFactionName(String factionName)
    {
        this.factionName = factionName;
        return this;
    }

    public Instant getRentExpiryDateTime()
    {
        return rentExpiryDateTime;
    }

    public void setRentExpiryDateTime(Instant rentExpiryDateTime)
    {
        this.rentExpiryDateTime = rentExpiryDateTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return id == region.id &&
                name.equals(region.name) &&
                aabb.equals(region.aabb) &&
                factionName.equals(region.factionName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, aabb, factionName);
    }

    @Override
    public String toString()
    {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", aabb=" + aabb +
                ", price=" + pricePerDay +
                ", factionName='" + factionName + '\'' +
                ", rentBy='" + rentBy + '\'' +
                '}';
    }
}
