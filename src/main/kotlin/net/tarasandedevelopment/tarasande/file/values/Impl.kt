package net.tarasandedevelopment.tarasande.file.values

import net.tarasandedevelopment.tarasande.value.impl.ValueBind
import java.util.function.Function

class FileValuesBinds : FileValues("Binds", Function { it is ValueBind })
class FileValuesNonBinds : FileValues("Values", Function { it !is ValueBind })