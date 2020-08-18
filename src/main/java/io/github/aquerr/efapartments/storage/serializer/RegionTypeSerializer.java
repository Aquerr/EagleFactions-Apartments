package io.github.aquerr.efapartments.storage.serializer;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import io.github.aquerr.efapartments.model.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.TypeTokens;

public class RegionTypeSerializer implements TypeSerializer<Region>
{
    @Override
    @Nullable
    public Region deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException
    {
        if (!(type.getType() instanceof Region))
            throw new ObjectMappingException("Wrong type token!");

        final int regionId = value.getNode("id").getInt();
        final String regionName = value.getNode("name").getString();
        final Vector3i firstCorner = value.getNode("firstCorner").getValue(TypeTokens.VECTOR_3I_TOKEN);
        final Vector3i secondCorner = value.getNode("secondCorner").getValue(TypeTokens.VECTOR_3I_TOKEN);
        final AABB aabb = new AABB(firstCorner, secondCorner);
        final String faction = value.getNode("faction").getString();
        final float price = value.getNode("price").getFloat();

        return new Region(regionId, regionName, faction, aabb, price);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Region obj, @NonNull ConfigurationNode value) throws ObjectMappingException
    {
        value.getNode("id").setValue(obj.getId());
        value.getNode("name").setValue(obj.getName());
        value.getNode("firstCorner").setValue(obj.getAabb().getMin().toInt());
        value.getNode("secondCorner").setValue(obj.getAabb().getMax().toInt());
        value.getNode("faction").setValue(obj.getFactionName());
        value.getNode("price").setValue(obj.getPricePerDay());
    }
}
