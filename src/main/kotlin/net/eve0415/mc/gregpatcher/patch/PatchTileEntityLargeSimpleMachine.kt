package net.eve0415.mc.gregpatcher.patch

import gregicadditions.machines.multi.simple.TileEntityLargeBenderAndForming
import gregicadditions.machines.multi.simple.TileEntityLargeMultiUse
import gregtech.api.items.metaitem.MetaItem
import gregtech.api.recipes.Recipe
import gregtech.api.recipes.RecipeMap
import net.eve0415.mc.gregpatcher.Patch
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode
import java.util.stream.IntStream

// Original: gregicadditions/machines/multi/simple/TileEntityLargeBenderAndForming.java
// Original: gregicadditions/machines/multi/simple/TileEntityLargeMultiUse.java

class PatchTileEntityLargeSimpleMachine(inputClass: ByteArray) : Patch(inputClass) {
    override fun patch(): Boolean {
        val onScrewdriverClick = findMethod("onScrewdriverClick")!!.instructions

        onScrewdriverClick.clear()
        onScrewdriverClick.add(VarInsnNode(ALOAD, 0))
        onScrewdriverClick.add(InsnNode(DUP))
        onScrewdriverClick.add(InsnNode(DUP))
        onScrewdriverClick.add(FieldInsnNode(GETFIELD, classNode.name, "pos", "I"))
        onScrewdriverClick.add(VarInsnNode(ALOAD, 1))
        onScrewdriverClick.add(
            FieldInsnNode(
                GETSTATIC,
                classNode.name,
                "possibleRecipe",
                "[Lgregtech/api/recipes/RecipeMap;"
            )
        )
        onScrewdriverClick.add(
            MethodInsnNode(
                INVOKESTATIC,
                hookClass,
                "changeMode",
                "(L${classNode.name};ILnet/minecraft/entity/player/EntityPlayer;[Lgregtech/api/recipes/RecipeMap;)I",
                false
            )
        )
        onScrewdriverClick.add(FieldInsnNode(PUTFIELD, classNode.name, "pos", "I"))

        onScrewdriverClick.add(VarInsnNode(ALOAD, 0))
        onScrewdriverClick.add(
            FieldInsnNode(
                GETFIELD,
                classNode.name,
                "recipeMapWorkable",
                "Lgregtech/api/capability/impl/MultiblockRecipeLogic;"
            )
        )
        onScrewdriverClick.add(VarInsnNode(ALOAD, 0))
        onScrewdriverClick.add(FieldInsnNode(GETFIELD, classNode.name, "pos", "I"))
        onScrewdriverClick.add(
            MethodInsnNode(
                INVOKESTATIC,
                hookClass,
                "getRecipeMap",
                "(I)Lgregtech/api/recipes/RecipeMap;",
                false
            )
        )
        onScrewdriverClick.add(
            FieldInsnNode(
                PUTFIELD,
                "gregtech/api/capability/impl/MultiblockRecipeLogic",
                "recipeMap",
                "Lgregtech/api/recipes/RecipeMap;"
            )
        )

        onScrewdriverClick.add(VarInsnNode(ALOAD, 0))
        onScrewdriverClick.add(
            FieldInsnNode(
                GETFIELD,
                classNode.name,
                "recipeMapWorkable",
                "Lgregtech/api/capability/impl/MultiblockRecipeLogic;"
            )
        )
        onScrewdriverClick.add(InsnNode(DUP))
        onScrewdriverClick.add(
            FieldInsnNode(
                GETFIELD,
                "gregtech/api/capability/impl/MultiblockRecipeLogic",
                "previousRecipe",
                "Lgregtech/api/recipes/Recipe;"
            )
        )
        onScrewdriverClick.add(
            MethodInsnNode(
                INVOKESTATIC,
                hookClass,
                "setRecipe",
                "(Lgregtech/api/recipes/Recipe;)Lgregtech/api/recipes/Recipe;",
                false
            )
        )
        onScrewdriverClick.add(
            FieldInsnNode(
                PUTFIELD,
                "gregtech/api/capability/impl/MultiblockRecipeLogic",
                "previousRecipe",
                "Lgregtech/api/recipes/Recipe;"
            )
        )

        onScrewdriverClick.add(
            MethodInsnNode(
                INVOKESTATIC,
                hookClass,
                "didChangeMode",
                "()Z",
                false
            )
        )
        onScrewdriverClick.add(InsnNode(IRETURN))

        return true
    }


    companion object {
        private var BenderForm: TileEntityLargeBenderAndForming? = null
        private var Multi: TileEntityLargeMultiUse? = null
        private var mode: Int = -1
        private var defaultRecipes: Array<RecipeMap<*>>? = null

        @JvmStatic
        fun changeMode(
            instance: TileEntityLargeBenderAndForming,
            pos: Int,
            playerIn: EntityPlayer,
            possibleRecipe: Array<RecipeMap<*>>
        ): Int {
            BenderForm = instance
            return changeMode(pos, playerIn, possibleRecipe)
        }

        @JvmStatic
        fun changeMode(
            instance: TileEntityLargeMultiUse,
            pos: Int,
            playerIn: EntityPlayer,
            possibleRecipe: Array<RecipeMap<*>>
        ): Int {
            Multi = instance
            return changeMode(pos, playerIn, possibleRecipe)
        }

        private fun changeMode(
            pos: Int,
            playerIn: EntityPlayer,
            possibleRecipe: Array<RecipeMap<*>>
        ): Int {
            defaultRecipes = possibleRecipe

            if ((BenderForm?.world?.isRemote ?: Multi?.world?.isRemote) == true) return pos

            val isEmpty = if (BenderForm != null) BenderForm?.inputInventory?.let {
                IntStream.range(0, it.slots)
                    .mapToObj { i: Int -> BenderForm!!.inputInventory.getStackInSlot(i) }
                    .allMatch { obj: ItemStack -> obj.isEmpty || obj.item.equals(MetaItem.getMetaItems().first()) }
            } else Multi?.inputInventory?.let {
                IntStream.range(0, it.slots)
                    .mapToObj { i: Int -> Multi!!.inputInventory.getStackInSlot(i) }
                    .allMatch { obj: ItemStack -> obj.isEmpty || obj.item.equals(MetaItem.getMetaItems().first()) }
            }
            if (isEmpty == false) return pos

            mode =
                if (playerIn.isSneaking) {
                    (if (pos == 0) possibleRecipe.size else pos).minus(1)
                } else {
                    (pos.plus(1)).rem(possibleRecipe.size)
                }

            return mode
        }

        @JvmStatic
        fun getRecipeMap(pos: Int): RecipeMap<*> {
            return defaultRecipes!![if (mode == -1) pos else mode]
        }

        @JvmStatic
        fun setRecipe(recipe: Recipe?): Recipe? {
            return if (mode == -1) recipe else null
        }

        @JvmStatic
        fun didChangeMode(): Boolean {
            if (mode == -1) return false

            if (BenderForm != null) BenderForm!!.recipeMap = defaultRecipes!![mode]
            if (Multi != null) Multi!!.recipeMap = defaultRecipes!![mode]

            return true
        }
    }
}
