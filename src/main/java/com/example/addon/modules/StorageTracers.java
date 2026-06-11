package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.world.level.block.entity.BlockEntity;

public class StorageTracers extends Module {

    public StorageTracers() {
        super(AddonTemplate.CATEGORY, "storage-tracers", "Draws tracers to storage blocks.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.level == null) return;

        for (BlockEntity be : mc.level.blockEntityList) {

            if (be instanceof ChestBlockEntity
                || be instanceof BarrelBlockEntity
                || be instanceof ShulkerBoxBlockEntity) {

                event.renderer.line(
                    mc.player.position(),
                    be.getBlockPos().getCenter(),
                    0, 200, 255, 255
                );
            }
        }
    }
}
