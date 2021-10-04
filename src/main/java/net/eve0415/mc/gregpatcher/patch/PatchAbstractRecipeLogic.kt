package net.eve0415.mc.gregpatcher.patch

import net.eve0415.mc.gregpatcher.Patch
import org.objectweb.asm.Opcodes.ACC_PUBLIC

// Original: gregtech/api/capability/impl/AbstractRecipeLogic.java

class PatchAbstractRecipeLogic(inputClass: ByteArray) : Patch(inputClass) {
    override fun patch(): Boolean {
        val recipeMap = findField("recipeMap")
        recipeMap?.access = ACC_PUBLIC

        val previousRecipe = findField("previousRecipe")
        previousRecipe?.access = ACC_PUBLIC

        return true
    }
}
