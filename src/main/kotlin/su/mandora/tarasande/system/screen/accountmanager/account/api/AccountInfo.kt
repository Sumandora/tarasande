package su.mandora.tarasande.system.screen.accountmanager.account.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountInfo(val name: String, val inherit: Boolean = true)
