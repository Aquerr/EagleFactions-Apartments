package io.github.aquerr.efapartments.model;

import com.flowpowered.math.vector.Vector3i;

public class SelectionPoints
{
    private Vector3i firstPoint;
    private Vector3i secondPoint;

    public SelectionPoints(final Vector3i firstPoint, final Vector3i secondPoint)
    {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    public void setFirstPoint(Vector3i firstPoint)
    {
        this.firstPoint = firstPoint;
    }

    public void setSecondPoint(Vector3i secondPoint)
    {
        this.secondPoint = secondPoint;
    }

    public Vector3i getFirstPoint()
    {
        return firstPoint;
    }

    public Vector3i getSecondPoint()
    {
        return secondPoint;
    }
}
