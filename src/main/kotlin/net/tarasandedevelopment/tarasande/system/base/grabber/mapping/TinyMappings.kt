package net.tarasandedevelopment.tarasande.system.base.grabber.mapping

import net.fabricmc.mapping.tree.TinyMappingFactory
import java.io.BufferedReader
import java.io.InputStreamReader

object TinyMappings {

    private val tinyTree = TinyMappingFactory.load(BufferedReader(InputStreamReader(TinyMappings::class.java.getResourceAsStream("/mappings/mappings.tiny")!!)))

    fun mapClassName(official: String): String {
        return tinyTree.classes.firstOrNull { it.getName("official") == official }?.getName("named") ?: official
    }

    fun mapMethodName(owner: String, official: String, desc: String): String {
        return tinyTree.classes.firstOrNull { it.getName("official") == owner }?.methods?.firstOrNull { it.getName("official") == official && it.getDescriptor("intermediary") == desc }?.getName("named") ?: official
    }

    fun mapFieldName(owner: String, official: String): String {
        return tinyTree.classes.firstOrNull { it.getName("official") == owner }?.fields?.firstOrNull { it.getName("official") == official }?.getName("named") ?: official
    }
}