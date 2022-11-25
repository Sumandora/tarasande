package su.mandora.codechecker.check.impl.source

import su.mandora.codechecker.check.CheckSource

class CheckPluralPackage : CheckSource("Plural Package") {
    override fun run() {
        allSources().forEach {  file ->
            if (file.extension == "java" || file.extension == "kt") {
                read(file).also { content ->
                    content.split("\n").filter { it.startsWith("package") }.forEach {
                        if(it.let { if(file.extension == "java") it.substring(0, it.length - 1) /* semicolon */ else it }.split(" ")[1].split(".").any { it.endsWith("s") })
                            violation(file, content.indexOf("package"), "Package name is a plural")
                    }
                }
            }
        }
    }
}