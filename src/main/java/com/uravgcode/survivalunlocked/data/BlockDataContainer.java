package com.uravgcode.survivalunlocked.data;

import com.uravgcode.survivalunlocked.SurvivalUnlocked;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public final class BlockDataContainer implements PersistentDataContainer {
    private final NamespacedKey key;
    private final PersistentDataContainer blockDataContainer;
    private final PersistentDataContainer chunkDataContainer;

    public BlockDataContainer(@NotNull Block block) {
        this(block.getLocation());
    }

    public BlockDataContainer(@NotNull Location location) {
        final int x = location.getBlockX() & 0xF;
        final int y = location.getBlockY();
        final int z = location.getBlockZ() & 0xF;

        key = new NamespacedKey(SurvivalUnlocked.instance(), "x" + x + "_y" + y + "_z" + z);
        chunkDataContainer = location.getChunk().getPersistentDataContainer();

        if (chunkDataContainer.has(key, PersistentDataType.TAG_CONTAINER)) {
            blockDataContainer = chunkDataContainer.get(key, PersistentDataType.TAG_CONTAINER);
        } else {
            blockDataContainer = chunkDataContainer.getAdapterContext().newPersistentDataContainer();
        }
    }

    @Override
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        blockDataContainer.set(key, type, value);
        chunkDataContainer.set(this.key, PersistentDataType.TAG_CONTAINER, blockDataContainer);
    }

    @Override
    public void remove(@NotNull NamespacedKey key) {
        blockDataContainer.remove(key);
        if (blockDataContainer.isEmpty()) {
            chunkDataContainer.remove(this.key);
        } else {
            chunkDataContainer.set(this.key, PersistentDataType.TAG_CONTAINER, blockDataContainer);
        }
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean clear) throws IOException {
        blockDataContainer.readFromBytes(bytes, clear);
        if (blockDataContainer.isEmpty()) {
            chunkDataContainer.remove(this.key);
        } else {
            chunkDataContainer.set(this.key, PersistentDataType.TAG_CONTAINER, blockDataContainer);
        }
    }

    @Override
    public <P, C> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type) {
        return blockDataContainer.has(key, type);
    }

    @Override
    public boolean has(@NotNull NamespacedKey key) {
        return blockDataContainer.has(key);
    }

    @Override
    public <P, C> @Nullable C get(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type) {
        return blockDataContainer.get(key, type);
    }

    @Override
    public <P, C> C getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, C defaultValue) {
        return blockDataContainer.getOrDefault(key, type, defaultValue);
    }

    @Override
    public @NotNull Set<NamespacedKey> getKeys() {
        return blockDataContainer.getKeys();
    }

    @Override
    public boolean isEmpty() {
        return blockDataContainer.isEmpty();
    }

    @Override
    public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
        blockDataContainer.copyTo(other, replace);
    }

    @Override
    public @NotNull PersistentDataAdapterContext getAdapterContext() {
        return blockDataContainer.getAdapterContext();
    }

    @Override
    public byte @NotNull [] serializeToBytes() throws IOException {
        return blockDataContainer.serializeToBytes();
    }

    @Override
    public int getSize() {
        return blockDataContainer.getSize();
    }
}
