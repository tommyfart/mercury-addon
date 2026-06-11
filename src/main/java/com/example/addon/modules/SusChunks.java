package com.example.addon.modules;

import com.example.addon.AddonTemplate;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;

import java.util.HashSet;
import java.util.Set;

public class SusChunks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> threshold = sgGeneral.add(
        new IntSetting.Builder()
            .name("threshold")
            .description("Score needed before a chunk is considered a stash.")
            .defaultValue(25)
            .range(1, 200)
            .build()
    );

    private final Set<String> foundChunks = new HashSet<>();

    private long lastScan;

    public SusChunks() {
        super(
            AddonTemplate.CATEGORY,
            "sus-chunks",
            "Detects suspicious storage-heavy chunks."
        );
    }

    @Override
    public void onActivate() {
        foundChunks.clear();
        lastScan = 0;
    }

    @Override
    public void onDeactivate() {
        foundChunks.clear();
    }

    @EventHandler
private void onTick(TickEvent.Post event) {
    if (mc.level == null) return;

    long now = System.currentTimeMillis();

    if (now - lastScan < 3000) return;
    lastScan = now;

    scanChunks();
}

    private void scanChunks() {
        Set<String> checked = new HashSet<>();

        for (BlockEntity be : Utils.blockEntities()) {
            BlockPos pos = be.getBlockPos();

            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;

            String chunkId = chunkX + "," + chunkZ;

            if (!checked.add(chunkId)) continue;

            int score = getChunkScore(chunkX, chunkZ);

            if (score >= threshold.get() && !foundChunks.contains(chunkId)) {
                foundChunks.add(chunkId);

                int x = (chunkX << 4) + 8;
                int z = (chunkZ << 4) + 8;

                ChatUtils.info(
                    "§cStash Found! §7X: " + x +
                    " Z: " + z +
                    " Score: " + score
                );
            }
        }
    }

    private int getChunkScore(int chunkX, int chunkZ) {
        int score = 0;

        for (BlockEntity be : Utils.blockEntities()) {
            BlockPos pos = be.getBlockPos();

            if ((pos.getX() >> 4) != chunkX) continue;
            if ((pos.getZ() >> 4) != chunkZ) continue;

            if (be instanceof ShulkerBoxBlockEntity) score += 15;
            else if (be instanceof ChestBlockEntity) score += 5;
            else if (be instanceof BarrelBlockEntity) score += 5;
            else if (be instanceof HopperBlockEntity) score += 2;
            else if (be instanceof EnderChestBlockEntity) score += 8;
        }

        return score;
    }
}
