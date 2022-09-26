package su.mandora.tarasande.file.values

import su.mandora.tarasande.value.ValueBind
import java.util.function.Function

class FileValuesBinds : FileValues("Binds", Function { it is ValueBind })
class FileValuesNonBinds : FileValues("Values", Function { it !is ValueBind })