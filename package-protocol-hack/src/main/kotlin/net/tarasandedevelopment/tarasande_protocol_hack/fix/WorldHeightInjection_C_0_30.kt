package net.tarasandedevelopment.tarasande_protocol_hack.fix

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type
import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.storage.ClassicLevelStorage
import java.util.*

object WorldHeightInjection_C_0_30 {

    fun handleJoinGame(parentRemapper: PacketRemapper): PacketRemapper {
        return object : PacketRemapper() {
            override fun registerMap() {
                handler { wrapper: PacketWrapper ->
                    parentRemapper.remap(wrapper)
                    if (wrapper.isCancelled) return@handler
                    if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.c0_28toc0_30)) {
                        for (dimension in wrapper.get(Type.NBT, 0).get<CompoundTag>("minecraft:dimension_type")!!.get<ListTag>("value")!!) {
                            changeDimensionTagHeight(wrapper.user(), (dimension as CompoundTag).get("element"))
                        }
                        changeDimensionTagHeight(wrapper.user(), wrapper.get(Type.NBT, 1))
                    }
                }
            }
        }
    }

    fun handleRespawn(parentRemapper: PacketRemapper): PacketRemapper {
        return object : PacketRemapper() {
            override fun registerMap() {
                handler { wrapper: PacketWrapper ->
                    parentRemapper.remap(wrapper)
                    if (wrapper.isCancelled) return@handler
                    if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.c0_28toc0_30)) {
                        changeDimensionTagHeight(wrapper.user(), wrapper.get(Type.NBT, 0))
                    }
                }
            }
        }
    }

    fun handleChunkData(parentRemapper: PacketRemapper): PacketRemapper {
        return object : PacketRemapper() {
            override fun registerMap() {
                handler { wrapper: PacketWrapper ->
                    parentRemapper.remap(wrapper)
                    if (wrapper.isCancelled) return@handler
                    if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.c0_28toc0_30)) {
                        wrapper.resetReader()
                        val chunk = wrapper.read(Chunk1_17Type(16))
                        wrapper.write(Chunk1_17Type(chunk.sections.size), chunk)
                        val heightProvider = Via.getManager().providers.get(ClassicWorldHeightProvider::class.java)
                        if (chunk.sections.size < heightProvider!!.getMaxChunkSectionCount(wrapper.user())) { // Increase available sections to match new world height
                            val newArray = arrayOfNulls<ChunkSection>(heightProvider.getMaxChunkSectionCount(wrapper.user()).toInt())
                            System.arraycopy(chunk.sections, 0, newArray, 0, chunk.sections.size)
                            chunk.sections = newArray
                        }
                        val chunkMask = BitSet()
                        for (i in chunk.sections.indices) {
                            if (chunk.sections[i] != null) chunkMask.set(i)
                        }
                        chunk.chunkMask = chunkMask
                        val newBiomeData = IntArray(chunk.sections.size * 4 * 4 * 4)
                        System.arraycopy(chunk.biomeData, 0, newBiomeData, 0, chunk.biomeData.size)
                        for (i in 64 until chunk.sections.size * 4) { // copy top layer of old biome data all the way to max world height
                            System.arraycopy(chunk.biomeData, chunk.biomeData.size - 16, newBiomeData, i * 16, 16)
                        }
                        chunk.biomeData = newBiomeData
                        chunk.heightMap = CompoundTag() // rip heightmap :(
                    }
                }
            }
        }
    }

    fun handleUpdateLight(parentRemapper: PacketRemapper): PacketRemapper {
        val classicLightHandler: PacketRemapper = object : PacketRemapper() {
            override fun registerMap() {
                map(Type.VAR_INT) // x
                map(Type.VAR_INT) // y
                map(Type.BOOLEAN) // trust edges
                handler { wrapper: PacketWrapper ->
                    wrapper.read(Type.VAR_INT) // sky light mask
                    wrapper.read(Type.VAR_INT) // block light mask
                    val emptySkyLightMask = wrapper.read(Type.VAR_INT) // empty sky light mask
                    val emptyBlockLightMask = wrapper.read(Type.VAR_INT) // empty block light mask
                    val level = wrapper.user().get(ClassicLevelStorage::class.java)!!.classicLevel
                    val heightProvider = Via.getManager().providers.get(ClassicWorldHeightProvider::class.java)
                    var sectionYCount = level.sizeY shr 4
                    if (level.sizeY % 16 != 0) sectionYCount++
                    if (sectionYCount > heightProvider!!.getMaxChunkSectionCount(wrapper.user())) {
                        sectionYCount = heightProvider.getMaxChunkSectionCount(wrapper.user()).toInt()
                    }
                    val lightArrays: MutableList<ByteArray> = ArrayList()
                    while (wrapper.isReadable(Type.BYTE_ARRAY_PRIMITIVE, 0)) {
                        lightArrays.add(wrapper.read(Type.BYTE_ARRAY_PRIMITIVE))
                    }
                    var skyLightCount = 16
                    var blockLightCount = sectionYCount
                    when (lightArrays.size) {
                        16 + 0 + 2 -> {
                            blockLightCount = 0
                        }
                        16 + sectionYCount + 2 -> {
                        }
                        sectionYCount + sectionYCount + 2 -> {
                            skyLightCount = sectionYCount
                        }
                    }
                    skyLightCount += 2 // Chunk below 0 and above 255
                    val skyLightMask = BitSet()
                    val blockLightMask = BitSet()
                    skyLightMask[0] = skyLightCount
                    blockLightMask[0] = blockLightCount
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, skyLightMask.toLongArray())
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, blockLightMask.toLongArray())
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, LongArray(emptySkyLightMask))
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, LongArray(emptyBlockLightMask))
                    wrapper.write(Type.VAR_INT, skyLightCount)
                    for (i in 0 until skyLightCount) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, lightArrays.removeAt(0))
                    }
                    wrapper.write(Type.VAR_INT, blockLightCount)
                    for (i in 0 until blockLightCount) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, lightArrays.removeAt(0))
                    }
                }
            }
        }
        return object : PacketRemapper() {
            override fun registerMap() {
                handler { wrapper: PacketWrapper ->
                    if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.c0_28toc0_30)) {
                        classicLightHandler.remap(wrapper)
                    } else {
                        parentRemapper.remap(wrapper)
                    }
                }
            }
        }
    }

    private fun changeDimensionTagHeight(user: UserConnection, tag: CompoundTag?) {
        tag!!.put("height", IntTag(Via.getManager().providers.get(ClassicWorldHeightProvider::class.java)!!.getMaxChunkSectionCount(user).toInt() shl 4))
    }
}
