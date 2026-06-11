package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.world.level.block.entity.*;
import net.minecraft.core.BlockPos;

public class StorageEsp extends Module {

    public StorageEsp() {
        super(AddonTemplate.CATEGORY, "storage-esp", "Highlights storage blocks.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.level == null) return;

        for (BlockEntity be : mc.level.blockEntityList) {
            if (isStorage(be)) {
                BlockPos pos = be.getBlockPos();

                event.renderer.box(
                    pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1,
                    0, 255, 0, 80
                );
            }
        }
    }

    private boolean isStorage(BlockEntity be) {
        return be instanceof ChestBlockEntity
            || be instanceof BarrelBlockEntity
            || be instanceof ShulkerBoxBlockEntity
            || be instanceof HopperBlockEntity
            || be instanceof FurnaceBlockEntity
            || be instanceof DispenserBlockEntity
            || be instanceof DropperBlockEntity
            || be instanceof EnderChestBlockEntity;
    }
}
