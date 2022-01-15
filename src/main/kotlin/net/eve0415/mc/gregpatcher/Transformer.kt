package net.eve0415.mc.gregpatcher

import net.eve0415.mc.gregpatcher.patch.PatchAbstractRecipeLogic
import net.eve0415.mc.gregpatcher.patch.PatchCTRecipeBuilder
import net.eve0415.mc.gregpatcher.patch.PatchMultiRecipeProvider
import net.eve0415.mc.gregpatcher.patch.PatchTileEntityLargeSimpleMachine
import net.minecraft.launchwrapper.IClassTransformer

class Transformer : IClassTransformer {
    override fun transform(name: String, transformedName: String, bytes: ByteArray): ByteArray {
        return when (transformedName) {
            "gregtech.api.capability.impl.AbstractRecipeLogic" -> {
                GregPatcher.LOGGER.info("Patching AbstractRecipeLogic from GTCE")
                PatchAbstractRecipeLogic(bytes).apply()!!
            }
            "gregtech.api.recipes.crafttweaker.CTRecipeBuilder" -> {
                GregPatcher.LOGGER.info("Patching CTRecipeBuilder from GTCE")
                PatchCTRecipeBuilder(bytes).apply()!!
            }
            "gregicadditions.theoneprobe.MultiRecipeProvider" -> {
                GregPatcher.LOGGER.info("Patching MultiRecipeProvider from GA")
                PatchMultiRecipeProvider(bytes).apply()!!
            }
            "gregicadditions.machines.multi.simple.TileEntityLargeBenderAndForming",
            "gregicadditions.machines.multi.simple.TileEntityLargeMultiUse" -> {
                GregPatcher.LOGGER.info("Patching Large Simple Machine from GA")
                PatchTileEntityLargeSimpleMachine(bytes).apply()!!
            }
            else -> bytes
        }
    }
}
