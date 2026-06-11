package com.example.addon.modules;

import com.example.addon.AddonTemplate;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.ColorSetting;

import meteordevelopment.orbit.EventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;

import java.util.HashSet;
import java.util.Set;

public class SusChunks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> threshold = sgGeneral.add(
        new IntSetting.Builder()
            .name("threshold")
            .description("Score required before a chunk is considered suspicious.")
            .defaultValue(25)
            .range(1, 500)
            .build()
    );

    private final Setting<SettingColor> color = sgRender.add(
        new ColorSetting.Builder()
            .name("color")
            .defaultValue(new SettingColor(255, 0, 0, 50))
            .build()
    );

    private final Set<String> notifiedChunks = new HashSet<>();

    public SusChunks() {
        super(AddonTemplate.CATEGORY, "sus-chunks", "Finds chunks with lots of storage blocks.");
    }

    @Override
    public void onDeactivate() {
        notifiedChunks.clear();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.level == null) return;

        Set<String> checkedChunks = new HashSet<>();

        for (BlockEntity be : mc.level.blockEntityList) {
            BlockPos pos = be.getBlockPos();

            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;

            String chunkId = chunkX + "," + chunkZ;

            if (checkedChunks.contains(chunkId)) continue;
            checkedChunks.add(chunkId);

            int score = getChunkScore(chunkX, chunkZ);

            if (score >= threshold.get()) {
                renderChunk(event, chunkX, chunkZ);

                if (!notifiedChunks.contains(chunkId)) {
                    notifiedChunks.add(chunkId);

                    int centerX = (chunkX << 4) + 8;
                    int centerZ = (chunkZ << 4) + 8;

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
    }

    private int getChunkScore(int chunkX, int chunkZ) {
        int score = 0;

        for (BlockEntity be : mc.level.blockEntityList) {
            BlockPos pos = be.getBlockPos();

            if ((pos.getX() >> 4) != chunkX) continue;
            if ((pos.getZ() >> 4) != chunkZ) continue;

            if (be instanceof ShulkerBoxBlockEntity) score += 15;
            else if (be instanceof ChestBlockEntity) score += 5;
            else if (be instanceof BarrelBlockEntity) score += 5;
            else if (be instanceof HopperBlockEntity) score += 2;
            else if (be instanceof EnderChestBlockEntity) score += 8;
            else if (be instanceof FurnaceBlockEntity) score += 1;
        }

        return score;
    }

    private void renderChunk(Render3DEvent event, int chunkX, int chunkZ) {
        double minX = chunkX * 16;
        double minZ = chunkZ * 16;

        double maxX = minX + 16;
        double maxZ = minZ + 16;

        event.renderer.box(
            minX,
            mc.player.getY() - 2,
            minZ,
            maxX,
            mc.player.getY() + 10,
            maxZ,
            color.get(),
            color.get(),
            ShapeMode.Both,
            0
        );
    }
}
