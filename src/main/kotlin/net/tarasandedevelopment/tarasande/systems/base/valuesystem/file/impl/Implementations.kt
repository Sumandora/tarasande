package net.tarasandedevelopment.tarasande.systems.base.valuesystem.file.impl

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.file.FileValues
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBind
import java.util.function.Function

class FileValuesBinds : FileValues("Binds", Function { it is ValueBind })
class FileValuesNonBinds : FileValues("Values", Function { it !is ValueBind })