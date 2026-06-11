package com.example.addon.modules;

import com.example.addon.AddonTemplate;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
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
    private final Set<BlockPos> stashCenters = new HashSet<>();

    private final Color lineColor = new Color(255, 0, 0, 255);
    private final Color sideColor = new Color(255, 0, 0, 40);
    private final Color tracerColor = new Color(255, 255, 0, 255);

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
        stashCenters.clear();
        lastScan = 0;
    }

    @Override
    public void onDeactivate() {
        foundChunks.clear();
        stashCenters.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.level == null) return;

        long now = System.currentTimeMillis();

        if (now - lastScan < 3000) return;
        lastScan = now;

        scanChunks();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        for (BlockPos center : stashCenters) {
            double minX = center.getX() - 8;
            double minZ = center.getZ() - 8;

            double maxX = center.getX() + 8;
            double maxZ = center.getZ() + 8;

            event.renderer.box(
                minX,
                mc.player.getY() - 2,
                minZ,
                maxX,
                mc.player.getY() + 20,
                maxZ,
                sideColor,
                lineColor,
                ShapeMode.Both,
                0
            );

            event.renderer.line(
                RenderUtils.center.x,
                RenderUtils.center.y,
                RenderUtils.center.z,
                center.getX() + 0.5,
                center.getY() + 0.5,
                center.getZ() + 0.5,
                tracerColor
            );
        }

        for (BlockEntity be : Utils.blockEntities()) {
            if (!(be instanceof ChestBlockEntity
                || be instanceof BarrelBlockEntity
                || be instanceof ShulkerBoxBlockEntity
                || be instanceof EnderChestBlockEntity
                || be instanceof HopperBlockEntity)) continue;

            BlockPos pos = be.getBlockPos();

            event.renderer.box(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 1,
                pos.getZ() + 1,
                sideColor,
                lineColor,
                ShapeMode.Both,
                0
            );
        }
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

                int centerX = (chunkX << 4) + 8;
                int centerZ = (chunkZ << 4) + 8;

                stashCenters.add(
                    new BlockPos(
                        centerX,
                        pos.getY(),
                        centerZ
                    )
                );

                ChatUtils.info(
                    "§cStash Found! §7X: "
                        + centerX
                        + " Z: "
                        + centerZ
                        + " Score: "
                        + score
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
