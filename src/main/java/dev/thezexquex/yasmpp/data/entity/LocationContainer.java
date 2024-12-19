package dev.thezexquex.yasmpp.data.entity;

public record LocationContainer(
     String world,
     double x,
     double y,
     double z,
     float yaw,
     float pitch
) {}