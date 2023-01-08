package net.tarasandedevelopment.tarasande_chat_features.gatekeep

import com.mojang.authlib.minecraft.UserApiService
import net.minecraft.client.util.ProfileKeysImpl
import java.nio.file.Path
import java.util.*

class GatekeepTracker(userApiService: UserApiService, uuid: UUID?, root: Path) {
    private val profileKeys = ArrayList<ProfileKeysImpl>()

    init {
        val keyPackage = root.resolve("gatekeep").resolve(uuid.toString()).toFile()
        keyPackage.mkdirs()

        keyPackage.listFiles()?.forEach {
            profileKeys.add(GatekeepProfileKeys(userApiService, uuid, root, it))
        }
    }

    fun getOldestValidKey(): ProfileKeysImpl? {
        val keys = profileKeys.filter { it.fetchKeyPair().get().isPresent && it.fetchKeyPair().get().map { it.isExpired }.orElse(false) }
        if (keys.isEmpty()) {
            return null
        }
        return keys.minWith(Comparator.comparingLong { it.fetchKeyPair().get().get().refreshedAfter.toEpochMilli() })
    }
}
