package su.mandora.tarasande.system.screen.accountmanager.account.api

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class TextFieldInfo(val name: String, val hidden: Boolean, val default: String = "")