package com.example.addon;

import com.example.addon.modules.Mercury;
import com.example.addon.modules.StorageEsp;
import com.example.addon.modules.StorageTracers;
import com.example.addon.modules.SusChunks;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    public static final meteordevelopment.meteorclient.systems.modules.Category CATEGORY =
        new meteordevelopment.meteorclient.systems.modules.Category("Mercury");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Mercury Addon");

        Modules.get().add(new Mercury());
        Modules.get().add(new StorageEsp());
        Modules.get().add(new StorageTracers());
        Modules.get().add(new SusChunks());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
    }
}
