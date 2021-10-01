package net.eve0415.mc.gregpatcher.patch

import gregtech.api.recipes.RecipeBuilder
import gregtech.api.recipes.ingredients.IntCircuitIngredient
import net.eve0415.mc.gregpatcher.GregPatcher
import net.eve0415.mc.gregpatcher.Patch
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

// Original: gregtech/api/recipes/crafttweaker/CTRecipeBuilder.java

class PatchCTRecipeBuilder(inputClass: ByteArray?) : Patch(inputClass) {
    override fun patch(): Boolean {
        var insertionPoint: AbstractInsnNode? = null
        val circuit = findMethod("circuit")!!.instructions

        run {
            val it: ListIterator<AbstractInsnNode> = circuit.iterator()
            while (it.hasNext()) {
                val insnNode: AbstractInsnNode = it.next()
                if (insnNode.opcode == GETFIELD) {
                    insertionPoint = insnNode
                    while (it.hasNext()) {
                        val insn: AbstractInsnNode = it.next()
                        circuit.remove(insn)
                        if (insn.opcode == INVOKEVIRTUAL)
                            break
                    }
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
        newInst.add(
            FieldInsnNode(
                GETFIELD,
                "gregtech/api/recipes/crafttweaker/CTRecipeBuilder",
                "backingBuilder",
                "Lgregtech/api/recipes/RecipeBuilder;"
            )
        )
        newInst.add(VarInsnNode(ILOAD, 1))
        newInst.add(
            MethodInsnNode(
                INVOKESTATIC, hookClass, "fixCircuit",
                "(Lgregtech/api/recipes/RecipeBuilder;I)V",
                false
            )
        )
        circuit.insert(insertionPoint, newInst)

        return true
    }

    companion object {
        @JvmStatic
        fun fixCircuit(builder: RecipeBuilder<*>, configuration: Int) {
            builder.notConsumable(IntCircuitIngredient(configuration))
        }
    }
}
