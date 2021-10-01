package net.eve0415.mc.gregpatcher

import net.minecraft.launchwrapper.Launch
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

abstract class Patch(private val inputClassBytes: ByteArray?) : Opcodes {
    private val classNode: ClassNode
    protected val hookClass = getName(javaClass).replace("patch/(.+)Patch".toRegex(), "hook/$1Hook")

    init {
        val classReader = ClassReader(inputClassBytes)
        classNode = ClassNode()
        classReader.accept(classNode, 0)
    }

    fun apply(): ByteArray? {
        return if (patch()) {
            GregPatcher.LOGGER.info("{} succeeded", this.javaClass.simpleName)
            writeClass()
        } else {
            GregPatcher.LOGGER.error("{} failed", this.javaClass.simpleName)
            inputClassBytes
        }
    }

    private fun writeClass(): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    protected abstract fun patch(): Boolean

    private fun getName(clazz: Class<*>?): String {
        return Type.getInternalName(clazz)
    }

    protected fun getName(name: String, srgName: String?): String {
        return if (Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean) name else srgName!!
    }

    protected fun findMethod(methodName: String): MethodNode? {
        for (methodNode in classNode.methods) {
            if (methodNode.name == methodName) {
                return methodNode
            }
        }
        return null
    }

    protected fun findMethod(methodName: String, api: Int): MethodNode? {
        for (methodNode in classNode.methods) {
            if (methodNode.name == methodName && methodNode.access == api) {
                return methodNode
            }
        }
        return null
    }
}