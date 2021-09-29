package net.eve0415.mc.gregpatcher.patch

import gregicadditions.capabilities.IMultiRecipe
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.TextStyleClass
import net.eve0415.mc.gregpatcher.Patch
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode

// Original: gregicadditions/theoneprobe/MultiRecipeProvider.java

class PatchMultiRecipeProvider(inputClass: ByteArray?) : Patch(inputClass) {
    override fun patch(): Boolean {
        val addProbeInfo = findMethod("addProbeInfo", ACC_PROTECTED)!!.instructions

        addProbeInfo.clear()
        addProbeInfo.add(VarInsnNode(ALOAD, 1))
        addProbeInfo.add(VarInsnNode(ALOAD, 2))
        addProbeInfo.add(
            MethodInsnNode(
                INVOKESTATIC,
                hookClass,
                "fixProbeInfo",
                "(Lgregicadditions/capabilities/IMultiRecipe;Lmcjty/theoneprobe/api/IProbeInfo;)V",
                false
            )
        )
        addProbeInfo.add(InsnNode(RETURN))

        return true
    }

    companion object {
        @JvmStatic
        fun fixProbeInfo(iMultiRecipe: IMultiRecipe, iProbeInfo: IProbeInfo) {
            val recipes = iMultiRecipe.recipes
            for (i in recipes.indices) {
                iProbeInfo.text(
                    (if (iMultiRecipe.currentRecipe == i) TextStyleClass.INFOIMP else TextStyleClass.INFO).toString() +
                            "{*recipemap." +
                            recipes[i].getUnlocalizedName() +
                            ".name*}" +
                            if (iMultiRecipe.currentRecipe == i) " {*<*}" else ""
                )
            }
        }
    }
}