package net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtraInfo(val name: String, val alternativeLogin: Boolean = false)
