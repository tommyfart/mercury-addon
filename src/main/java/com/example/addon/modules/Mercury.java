package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;

public class Mercury extends Module {

    public final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> storageEsp = sgGeneral.add(new BoolSetting.Builder()
        .name("storage-esp")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> tracers = sgGeneral.add(new BoolSetting.Builder()
        .name("storage-tracers")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> susChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("sus-chunks")
        .defaultValue(true)
        .build()
    );

    public final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .defaultValue(128)
        .min(32)
        .max(512)
        .build()
    );

    public final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
        .name("chunk-threshold")
        .defaultValue(25)
        .min(1)
        .max(100)
        .build()
    );

    public Mercury() {
        super(AddonTemplate.CATEGORY, "Mercury", "Main stash finder control module.");
    }
}
