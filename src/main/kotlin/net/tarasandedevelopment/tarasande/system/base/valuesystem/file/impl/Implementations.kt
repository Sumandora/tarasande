package net.tarasandedevelopment.tarasande.system.base.valuesystem.file.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.file.FileValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import java.util.function.Function

class FileValuesBinds : FileValues("Binds", Function { it is ValueBind })
class FileValuesNonBinds : FileValues("Values", Function { it !is ValueBind })