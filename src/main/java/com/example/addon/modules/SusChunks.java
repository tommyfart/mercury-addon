package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.world.level.block.entity.*;
import net.minecraft.core.BlockPos;

public class SusChunks extends Module {

    public SusChunks() {
        super(AddonTemplate.CATEGORY, "sus-chunks", "Detects loot-dense chunks.");
    }

    private int getScore(BlockPos pos) {
        int score = 0;

        for (BlockEntity be : mc.level.blockEntityList) {
            BlockPos p = be.getBlockPos();

            if ((p.getX() >> 4) == (pos.getX() >> 4)
                && (p.getZ() >> 4) == (pos.getZ() >> 4)) {

                if (be instanceof ShulkerBoxBlockEntity) score += 15;
                if (be instanceof ChestBlockEntity) score += 5;
                if (be instanceof BarrelBlockEntity) score += 5;
                if (be instanceof HopperBlockEntity) score += 2;
            }
        }

        return score;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.level == null) return;

        for (BlockEntity be : mc.level.blockEntityList) {
            int score = getScore(be.getBlockPos());

            if (score >= 25) {
                BlockPos p = be.getBlockPos();

                event.renderer.box(
                    p.getX(), p.getY(), p.getZ(),
                    p.getX() + 1, p.getY() + 1, p.getZ() + 1,
                    255, 0, 0, 90
                );
            }
        }
    }
}
