package net.tarasandedevelopment.tarasande.transformation.mappings

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.mapping.tree.TinyMappingFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class TinyMappings {

    private val tinyTree =
        if(FabricLoader.getInstance().isDevelopmentEnvironment) {
            null
        } else {
            TinyMappingFactory.load(BufferedReader(InputStreamReader(TinyMappings::class.java.getResourceAsStream("/mappings/mappings.tiny")!!)))
        }

    fun mapClassName(named: String): String? {
        if(tinyTree == null)
            return named

        return tinyTree.classes.firstOrNull { it.getName("intermediary") == named }?.getName("named")
    }

    fun mapMethodName(owner: String, named: String, desc: String): String? {
        if(tinyTree == null)
            return named

        return tinyTree.classes.firstOrNull { it.getName("intermediary") == owner }?.methods?.firstOrNull { it.getName("intermediary") == named && it.getDescriptor("intermediary") == desc }?.getName("named")
    }

    fun mapFieldName(owner: String, named: String): String? {
        if(tinyTree == null)
            return named

        return tinyTree.classes.firstOrNull { it.getName("intermediary") == owner }?.fields?.firstOrNull { it.getName("intermediary") == named }?.getName("named")
    }

}