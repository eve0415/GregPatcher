package net.eve0415.mc.gregpatcher

import net.eve0415.mc.gregpatcher.patch.PatchMultiRecipeProvider
import net.minecraft.launchwrapper.IClassTransformer

class Transformer : IClassTransformer {
    override fun transform(name: String, transformedName: String, bytes: ByteArray): ByteArray {
        return when (transformedName) {
            "gregicadditions.theoneprobe.MultiRecipeProvider" -> {
                GregPatcher.LOGGER.info("Patching MultiRecipeProvider from GA")
                PatchMultiRecipeProvider(bytes).apply()!!
            }
            else -> bytes
        }
    }
}