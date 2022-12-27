package net.tarasandedevelopment.tarasande.transformation

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.transformer.ext.IExtension
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext

class ExtensionTarasande : IExtension {
    override fun checkActive(environment: MixinEnvironment?) = true

    override fun preApply(context: ITargetClassContext?) {
        ManagerTransformer.transform(context?.classNode ?: return)
    }

    override fun postApply(context: ITargetClassContext?) {
    }

    override fun export(env: MixinEnvironment?, name: String?, force: Boolean, classNode: ClassNode?) {
    }
}