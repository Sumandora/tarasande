package su.mandora.codechecker.check.impl.source

import com.google.gson.Gson
import com.google.gson.JsonObject
import su.mandora.codechecker.check.CheckSource

class CheckUnregisteredMixin : CheckSource("Unregistered Mixin") {

    private val gson = Gson()

    override fun run() {
        allSources().filter { it.name.endsWith(".mixins.json") }.forEach {
            val jsonObject = gson.fromJson(read(it), JsonObject::class.java)
            val pkg = jsonObject.get("package").asString

            val allMixins = ArrayList<String>()
            jsonObject.getAsJsonArray("mixins")?.forEach {
                allMixins.add(it.asString)
            }
            jsonObject.getAsJsonArray("client")?.forEach {
                allMixins.add(it.asString)
            }

            allSources().forEach { file ->
                if (file.path.replace("/", ".").contains(pkg) && file.name.contains("Mixin")) {
                    if (allMixins.none { file.path.substring(0, file.path.length - file.extension.length - 1).replace("/", ".").endsWith(it) }) {
                        violation(file, read(file).indexOf("@Mixin"), "Seems to be unregistered")
                    }
                }
            }
        }
    }
}