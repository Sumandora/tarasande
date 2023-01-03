package net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.RegistryEntryArgumentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import java.util.function.Function

class CommandEnchant : Command("enchant") {

    private val notHoldingItem = SimpleCommandExceptionType(Text.literal("You are not holding any item in your hand"))

    private fun heldItem(): ItemStack? {
        mc.player?.mainHandStack?.apply { return this }
        mc.player?.offHandStack?.apply { return this }

        return null
    }

    private fun getTargetItem(): ItemStack {
        if (mc.player?.abilities?.creativeMode == false)
            throw notInCreative.create()

        heldItem().apply {
            if (this == null) {
                throw notHoldingItem.create()
            }
            return this
        }
    }

    private fun syncInventory() = mc.player?.playerScreenHandler?.sendContentUpdates()

    private fun singleEnchant(enchantment: Enchantment, level: Int) {
        getTargetItem().apply {
            EnchantmentHelper.set(EnchantmentHelper.get(this).apply {
                put(enchantment, level)
            }, this)
        }
        syncInventory()
    }

    private fun allEnchant(onlyPossible: Boolean = false, level: Function<Enchantment, Int>) {
        getTargetItem().apply {
            for (e in Registries.ENCHANTMENT.toList())
                if (!onlyPossible || e.isAcceptableItem(this))
                    singleEnchant(e, level.apply(e))
        }
        syncInventory()
    }

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        builder.then(literal("single").then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))?.then(
            literal("level").then(argument("level", IntegerArgumentType.integer())?.executes {
                val level = it.getArgument("level", Int::class.java)
                @Suppress("UNCHECKED_CAST")
                (it.getArgument("enchantment", RegistryEntry.Reference::class.java) as RegistryEntry.Reference<Enchantment>).value().apply {
                    singleEnchant(this, level)
                    printChatMessage("The enchantment [" + StringUtil.uncoverTranslation(this.translationKey) + "] at level [" + level + "] was added")
                }
                return@executes SUCCESS
            }).then(literal("max").executes {
                @Suppress("UNCHECKED_CAST")
                (it.getArgument("enchantment", RegistryEntry.Reference::class.java) as RegistryEntry.Reference<Enchantment>).value().apply {
                    singleEnchant(this, this.maxLevel)
                    printChatMessage("The enchantment [" + StringUtil.uncoverTranslation(this.translationKey) + "] at max level was added")
                }
                return@executes SUCCESS
            })
        )))

        builder.then(literal("all-possible").then(literal("level").then(
            argument("level", IntegerArgumentType.integer())?.executes { context ->
                context.getArgument("level", Int::class.java)?.apply {
                    allEnchant(true) {
                        return@allEnchant this
                    }
                    printChatMessage("All possible enchantments at level [$this] were added")
                }
                return@executes SUCCESS
            }
        ).then(literal("max").executes {
            allEnchant(true) {
                return@allEnchant it.maxLevel
            }
            printChatMessage("All possible enchantments at max level were added")
            return@executes SUCCESS
        })))

        builder.then(literal("all").then(literal("level").then(
            argument("level", IntegerArgumentType.integer())?.executes { context ->
                context.getArgument("level", Int::class.java)?.apply {
                    allEnchant(level = Function {
                        return@Function this
                    })
                    printChatMessage("All enchantments at level [$this] were added")
                }
                return@executes SUCCESS
            }).then(literal("max").executes {
            allEnchant(false) {
                return@allEnchant it.maxLevel
            }
            printChatMessage("All enchantments at max level were added")
            return@executes SUCCESS
        })))

        builder.then(literal("remove").then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))?.executes {
            @Suppress("UNCHECKED_CAST")
            val enchantment = (it.getArgument("enchantment", RegistryEntry.Reference::class.java) as RegistryEntry.Reference<Enchantment>).value()
            getTargetItem().apply {
                EnchantmentHelper.set(EnchantmentHelper.get(this).apply { remove(enchantment) }, this)
            }
            syncInventory()
            printChatMessage("The enchantment [" + StringUtil.uncoverTranslation(enchantment.translationKey) + "] was removed")
            return@executes SUCCESS
        }))

        builder.then(literal("clear").executes {
            getTargetItem().nbt?.remove("Enchantments")
            printChatMessage("All enchantments have been removed")
            return@executes SUCCESS
        })
        return builder
    }
}
