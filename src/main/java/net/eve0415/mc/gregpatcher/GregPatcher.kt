package net.eve0415.mc.gregpatcher

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@MCVersion("1.12.2")
@SortingIndex(1001)
@Mod(
    modid = GregPatcher.MOD_ID,
    name = GregPatcher.MOD_NAME,
    version = GregPatcher.VERSION,
    dependencies = GregPatcher.DEPENDENCIES,
    acceptableRemoteVersions = "*"
)
class GregPatcher : IFMLLoadingPlugin {
    companion object {
        const val MOD_ID = "gregpatcher"
        const val MOD_NAME = "GregPatcher"
        const val VERSION = "1.0-SNAPSHOT"
        const val DEPENDENCIES = "required:gtadditions;"
        val LOGGER: Logger = LogManager.getLogger("GregPatcher")
    }

    override fun getASMTransformerClass(): Array<String> {
        return arrayOf("net.eve0415.mc.gregpatcher.Transformer")
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(data: MutableMap<String, Any>?) {
    }

    override fun getAccessTransformerClass(): String? {
        return null
    }
}