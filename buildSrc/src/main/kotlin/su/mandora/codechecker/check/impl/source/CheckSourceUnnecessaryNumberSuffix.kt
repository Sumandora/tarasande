package su.mandora.codechecker.check.impl.source

import su.mandora.codechecker.check.CheckSource
import java.util.regex.Pattern

class CheckSourceUnnecessaryNumberSuffix : CheckSource("Unnecessary Number Suffix") {

    private val floatRegex = Pattern.compile("[^\\w\\s]([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+))d")

    override fun run() {
        allSources().forEach {
            if (it.extension == "java" || it.extension == "kt") {
                read(it).lowercase().also { content ->
                    val matcher = floatRegex.matcher(content)
                    while(matcher.find()) {
                        val index = matcher.end() - 1
                        violation(it, index, "Contains a useless number suffix")
                    }
                }
            }
        }
    }
}