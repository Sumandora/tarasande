package net.tarasandedevelopment.tarasande.system.feature.commandsystem.impl

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnchantmentArgumentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry
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

    private fun allEnchant(level: Function<Enchantment, Int>, onlyPossible: Boolean = false) {
        getTargetItem().apply {
            for (e in Registry.ENCHANTMENT.toList())
                if (!onlyPossible || e.isAcceptableItem(this))
                    singleEnchant(e, level.apply(e))
        }
        syncInventory()
    }

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        // single enchant
        builder.then(literal("single").then(argument("enchantment", EnchantmentArgumentType.enchantment())?.then(
            literal("level").then(argument("level", IntegerArgumentType.integer())?.executes {
                val level = it.getArgument("level", Int::class.java)

                it.getArgument("enchantment", Enchantment::class.java).apply {
                    singleEnchant(this, level)
                    printChatMessage("The Enchantment [" + StringUtil.uncoverTranslation(this.translationKey) + "] at level [" + level + "] was added")
                }
                return@executes success
            }).then(literal("max").executes {
                it.getArgument("enchantment", Enchantment::class.java).apply {
                    singleEnchant(this, this.maxLevel)
                    printChatMessage("The Enchantment [" + StringUtil.uncoverTranslation(this.translationKey) + "] at max level was added")
                }
                return@executes success
            })
        )))

        // all possible enchants
        builder.then(literal("all-possible").then(literal("level").then(
            argument("level", IntegerArgumentType.integer())?.executes { context ->
                context.getArgument("level", Int::class.java)?.apply {
                    allEnchant(level = Function {
                        return@Function this
                    }, true)
                    printChatMessage("All possible enchantments at level [$this] were added")
                }
                return@executes success
            }
        ).then(literal("max").executes {
            allEnchant(level = Function {
                return@Function it.maxLevel
            }, true)
            printChatMessage("All possible enchantments at max level were added")
            return@executes success
        })))

        // all enchants
        builder.then(literal("all").then(literal("level").then(
            argument("level", IntegerArgumentType.integer())?.executes { context ->
                context.getArgument("level", Int::class.java)?.apply {
                    allEnchant(level = Function {
                        return@Function this
                    })
                    printChatMessage("All enchantments at level [$this] were added")
                }
                return@executes success
            }).then(literal("max").executes {
            allEnchant(level = Function {
                return@Function it.maxLevel
            }, false)
            printChatMessage("All enchantments at max level were added")
            return@executes success
        })))

        // remove
        builder.then(literal("remove").then(argument("enchantment", EnchantmentArgumentType.enchantment())?.executes {
            val enchantment = it.getArgument("enchantment", Enchantment::class.java)
            getTargetItem().apply {
                EnchantmentHelper.set(EnchantmentHelper.get(this).apply { remove(enchantment) }, this)
            }
            syncInventory()
            printChatMessage("The Enchantment [" + StringUtil.uncoverTranslation(enchantment.translationKey) + "] was removed")
            return@executes success
        }))

        // clear
        builder.then(literal("clear").executes {
            getTargetItem().nbt?.remove("Enchantments")
            printChatMessage("All enchantments have been removed")
            return@executes success
        })

        return builder
    }
}
