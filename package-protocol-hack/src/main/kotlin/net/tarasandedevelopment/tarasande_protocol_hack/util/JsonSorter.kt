package net.tarasandedevelopment.tarasande_protocol_hack.util

import com.google.gson.JsonElement
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.io.StringWriter

object JsonSorter {

    fun toSortedString(json: JsonElement?): String {
        val stringWriter = StringWriter()
        val jsonWriter = JsonWriter(stringWriter)
        try {
            writeSorted(jsonWriter, json, Comparator.naturalOrder())
        } catch (var4: IOException) {
            throw AssertionError(var4)
        }
        return stringWriter.toString()
    }

    @Throws(IOException::class)
    fun writeSorted(writer: JsonWriter, json: JsonElement?, comparator: Comparator<String>?) {
        if (json != null && !json.isJsonNull) {
            if (json.isJsonPrimitive) {
                val jsonPrimitive = json.asJsonPrimitive
                if (jsonPrimitive.isNumber) {
                    writer.value(jsonPrimitive.asNumber)
                } else if (jsonPrimitive.isBoolean) {
                    writer.value(jsonPrimitive.asBoolean)
                } else {
                    writer.value(jsonPrimitive.asString)
                }
            } else {
                val var5: Iterator<*>
                if (json.isJsonArray) {
                    writer.beginArray()
                    var5 = json.asJsonArray.iterator()
                    while (var5.hasNext()) {
                        val jsonElement = var5.next() as JsonElement
                        writeSorted(writer, jsonElement, comparator)
                    }
                    writer.endArray()
                } else {
                    require(json.isJsonObject) { "Couldn't write " + json.javaClass }
                    writer.beginObject()
                    var5 = sort(json.asJsonObject.entrySet(), comparator).iterator()
                    while (var5.hasNext()) {
                        val (key, value) = var5.next() as Map.Entry<*, *>
                        writer.name(key as String)
                        writeSorted(writer, value as JsonElement, comparator)
                    }
                    writer.endObject()
                }
            }
        } else {
            writer.nullValue()
        }
    }

    private fun sort(entries: Collection<Map.Entry<String?, JsonElement?>?>, comparator: Comparator<String>?): Collection<Map.Entry<String?, JsonElement?>?> {
        return if (comparator == null) {
            entries
        } else {
            val list = ArrayList(entries)
            list.sortWith(java.util.Map.Entry.comparingByKey<String?, JsonElement>(comparator))
            list
        }
    }

}