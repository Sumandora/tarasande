package su.mandora.codechecker.check.impl.bytecode

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import su.mandora.codechecker.check.CheckBytecode

class CheckBytecodeAccessWidenerUsage : CheckBytecode("Access Widener Usage") {

    private val gson = Gson()

    override fun run() {
        val fabricModJson = getFile("resources", "fabric.mod.json")
        val jsonObject = gson.fromJson(fabricModJson.readBytes().decodeToString(), JsonObject::class.java)
        val accessWidener = getFile("resources", jsonObject.get("accessWidener").asString)
        val content = accessWidener.readBytes().decodeToString()

        for (line in content.split("\n")) {
            if (line.startsWith("#")) // comment
                continue
            if (line.startsWith("accessWidener")) // initializer
                continue
            if (line.isEmpty())
                continue
            if(line.endsWith("# @optimizer:ignore"))
                continue // manually mark certain lines as useful

            val parts = line.split(" ")

            when {
                line.startsWith("accessible field") -> {
                    if(parts[3].uppercase() == parts[3])
                        continue // We don't know whether a constant was used, when looking at bytecode
                    if (!allNodes().any {
                            it.methods.any {
                                @Suppress("LABEL_NAME_CLASH")
                                it.instructions.any {
                                    if (it is FieldInsnNode) {
                                        return@any /*it.owner == parts[2] &&*/ /* Check doesn't account for inheritance */ it.name == parts[3] && it.desc == parts[4]
                                    }
                                    return@any false
                                }
                            }
                        })
                        violation("\"$line\" is unnecessary")
                }
                line.startsWith("accessible method") -> {
                    if (!allNodes().any {
                            it.methods.any {
                                @Suppress("LABEL_NAME_CLASH")
                                it.instructions.any {
                                    if (it is MethodInsnNode) {
                                        return@any /*it.owner == parts[2] &&*/ /* Check doesn't account for inheritance */ it.name == parts[3] && it.desc == parts[4]
                                    }
                                    return@any false
                                }
                            }
                        })
                        violation("\"$line\" is unnecessary")
                }
                line.startsWith("accessible class") -> {
                    // unsupported :c
                }
                line.startsWith("mutable field") -> {
                    if(parts[3].uppercase() == parts[3])
                        continue // We don't know whether a constant was used, when looking at bytecode
                    if (!allNodes().any {
                            it.methods.any {
                                @Suppress("LABEL_NAME_CLASH")
                                it.instructions.any {
                                    if (it is FieldInsnNode && it.opcode == Opcodes.PUTFIELD) {
                                        return@any /*it.owner == parts[2] &&*/ /* Check doesn't account for inheritance */ it.name == parts[3] && it.desc == parts[4]
                                    }
                                    return@any false
                                }
                            }
                        })
                        violation("\"$line\" is unnecessary")
                }
            }
        }
    }
}