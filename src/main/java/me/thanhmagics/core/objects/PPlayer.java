package me.thanhmagics.core.objects;

import java.util.UUID;

public class PPlayer {
    private UUID uuid,inv = null;

    public PPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getInv() {
        return inv;
    }

    public void setInv(UUID inv) {
        this.inv = inv;
    }
}
