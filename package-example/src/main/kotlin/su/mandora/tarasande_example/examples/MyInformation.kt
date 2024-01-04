package su.mandora.tarasande_example.examples

import su.mandora.tarasande.system.screen.informationsystem.Information

class MyInformation : Information("My information", "My information") {
    override fun getMessage() = "Hello, world!"
}
