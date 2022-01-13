package net.eve0415.mc.gregpatcher.patch

import net.eve0415.mc.gregpatcher.GregPatcher
import net.eve0415.mc.gregpatcher.Patch
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

// Original: gregtech/api/capability/impl/AbstractRecipeLogic.java

class PatchAbstractRecipeLogic(inputClass: ByteArray) : Patch(inputClass) {
    override fun patch(): Boolean {
        val recipeMap = findField("recipeMap")
        recipeMap?.access = ACC_PUBLIC

        val previousRecipe = findField("previousRecipe")
        previousRecipe?.access = ACC_PUBLIC

        // This didn't work out so well on gregicadditions, but I'll leave it just in case
        var insertionPoint: AbstractInsnNode? = null
        val updateRecipeProgress = findMethod("updateRecipeProgress")!!.instructions

        run {
            val it: ListIterator<AbstractInsnNode> = updateRecipeProgress.iterator()
            while (it.hasNext()) {
                val insnNode: AbstractInsnNode = it.next()
                if (insnNode.opcode == IFGE) {
                    insertionPoint = insnNode
                    break
                }
            }
        }

        if (insertionPoint == null) {
            GregPatcher.LOGGER.warn("Could not find target instructions to patch. Skipping.")
            return false
        }

        val newInst = InsnList()
        newInst.add(VarInsnNode(ALOAD, 0))
        newInst.add(InsnNode(ICONST_0))
        newInst.add(
            FieldInsnNode(
                PUTFIELD,
                "gregtech/api/capability/impl/AbstractRecipeLogic",
                "hasNotEnoughEnergy",
                "Z"
            )
        )
        newInst.add(
            MethodInsnNode(
                INVOKESTATIC, hookClass, "check",
                "()V",
                false
            )
        )
        updateRecipeProgress.insert(insertionPoint, newInst)

        return true
    }
}
