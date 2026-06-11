package com.example.addon.modules;

import com.example.addon.AddonTemplate;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Mercury extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> storageEsp = sgGeneral.add(
        new BoolSetting.Builder()
            .name("storage-esp")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> tracers = sgGeneral.add(
        new BoolSetting.Builder()
            .name("tracers")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> stashAlerts = sgGeneral.add(
        new BoolSetting.Builder()
            .name("stash-alerts")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> threshold = sgGeneral.add(
        new IntSetting.Builder()
            .name("stash-threshold")
            .defaultValue(25)
            .range(1, 100)
            .build()
    );

    public Mercury() {
        super(
            AddonTemplate.CATEGORY,
            "mercury",
            "Base and stash finder."
        );
    }
}
