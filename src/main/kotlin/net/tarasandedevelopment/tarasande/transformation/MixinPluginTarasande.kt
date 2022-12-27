package net.tarasandedevelopment.tarasande.transformation

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import org.spongepowered.asm.mixin.transformer.ext.Extensions

class MixinPluginTarasande : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) {
        val transformer = MixinEnvironment.getCurrentEnvironment().activeTransformer
        val extensions = transformer.javaClass.declaredFields.first { it.type == Extensions::class.java }.let { it.isAccessible = true; it.get(transformer) as Extensions }
        extensions.add(ExtensionTarasande())
    }

    override fun getRefMapperConfig() = null

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?) = true

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {
    }

    override fun getMixins() = null

    override fun preApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }

    override fun postApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }
}