package com.stebars.cantswimupwaterfalls;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


@Mod(CantSwimUpWaterfallsMod.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CantSwimUpWaterfallsMod {
	public final static String MOD_ID = "cantswimupwaterfalls";

	public CantSwimUpWaterfallsMod() {
		MinecraftForge.EVENT_BUS.register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
	}
}
