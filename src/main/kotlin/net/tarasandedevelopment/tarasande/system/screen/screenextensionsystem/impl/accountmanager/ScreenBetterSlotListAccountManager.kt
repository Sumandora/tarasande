package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager

import com.mojang.authlib.minecraft.UserApiService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import it.unimi.dsi.fastutil.booleans.BooleanConsumer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.Bans
import net.minecraft.client.network.SocialInteractionsManager
import net.minecraft.client.util.ProfileKeys
import net.minecraft.client.util.Session
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.encryption.SignatureVerifier
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.injection.accessor.IRealmsPeriodicCheckers
import net.tarasandedevelopment.tarasande.screen.base.AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.base.EntryScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountSession
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.ManagerEnvironment
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.file.FileAccounts
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.subscreen.ScreenBetterAccount
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.subscreen.ScreenBetterProxy
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import org.apache.commons.lang3.RandomStringUtils
import su.mandora.event.EventDispatcher
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class ScreenBetterSlotListAccountManager : ScreenBetterSlotList(46, 10, 240, FontWrapper.fontHeight() * 5, null) { // set else where

    val accounts = ArrayList<Account>()
    var currentAccount: Account? = null

    var mainAccount: Int? = null

    private var loginThread: ThreadRunnableExposed? = null

    private var loginButton: ButtonWidget? = null
    private var removeButton: ButtonWidget? = null
    private var toggleMainButton: ButtonWidget? = null
    private var addButton: ButtonWidget? = null
    private var randomButton: ButtonWidget? = null

    var status = ""

    // @formatter:off
    val managerEnvironment  = ManagerEnvironment()
    val managerAzureApp     = ManagerAzureApp()
    val managerAccount      = ManagerAccount()
    // @formatter:on

    val screenBetterProxy = ScreenBetterProxy()

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            TarasandeMain.managerFile().add(FileAccounts(this))

            if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && mainAccount != null) {
                logIn(accounts[mainAccount!!])

                while (loginThread != null && loginThread!!.isAlive)
                    Thread.sleep(50L) // synchronize

                status = ""
            }
        }
    }

    fun selected(): Account? {
        if (this.slotList!!.selectedOrNull == null) return null

        return (this.slotList!!.selectedOrNull as EntryScreenBetterSlotListEntryAccount).account
    }

    override fun init() {
        this.provideElements(object : AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<EntryScreenBetterSlotListEntry> {
                return accounts.map { a -> EntryScreenBetterSlotListEntryAccount(a) }
            }
        })
        super.init()

        addDrawableChild(ButtonWidget(width / 2 - 152, height - 46 - 3, 100, 20, Text.of("Login")) { logIn(this.selected()) }.also { loginButton = it })
        addDrawableChild(ButtonWidget(width / 2 - 50, height - 46 - 3, 100, 20, Text.of("Remove")) {
            if (this.selected() == null) return@ButtonWidget

            if (accounts.indexOf(this.selected()!!) == mainAccount) mainAccount = null
            accounts.remove(this.selected())
            slotList?.reload()
            slotList?.setSelected(null)
        }.also { removeButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 52, height - 46 - 3, 100, 20, Text.of("Toggle main")) {
            val account = this.selected() ?: return@ButtonWidget

            if (account.session == null) {
                status = Formatting.RED.toString() + "Account hasn't been logged into yet"
            } else {
                val index = accounts.indexOf(account)
                if (mainAccount != index) {
                    mainAccount = index
                    status = Formatting.YELLOW.toString() + account.getDisplayName() + " is now the Main-Account"
                } else {
                    mainAccount = null
                    status = Formatting.YELLOW.toString() + account.getDisplayName() + " is no longer a Main-Account"
                }
            }
        }.also { toggleMainButton = it })

        addDrawableChild(ButtonWidget(width / 2 - 152, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount(this, "Direct Login") { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 - 50, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Random Account")) { logIn(accounts[ThreadLocalRandom.current().nextInt(accounts.size)]) }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 52, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount(this, "Add Account") { account ->
                accounts.add(account)
                this.slotList?.reload()
            })
        }.also { addButton = it })

        addDrawableChild(ButtonWidget(3, 3, 100, 20, Text.of("Proxy")) {
            MinecraftClient.getInstance().setScreen(screenBetterProxy.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
        })

        addDrawableChild(ButtonWidget(width - 103, 3, 100, 20, Text.of("Random session")) {
            logIn(AccountSession().also {
                it.username = RandomStringUtils.randomAlphanumeric(16)
                it.uuid = UUID.randomUUID().toString()
            })
        })

        tick()
    }

    override fun tick() {
        loginButton?.active = slotList?.selectedOrNull != null
        removeButton?.active = slotList?.selectedOrNull != null
        toggleMainButton?.active = slotList?.selectedOrNull != null
        if (slotList?.selectedOrNull != null) toggleMainButton?.active = true
        randomButton?.active = accounts.isNotEmpty()
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        this.renderTitle(matrices, "Account Manager")
        FontWrapper.textShadow(matrices, status, width / 2.0F, 2 * FontWrapper.fontHeight() * 2.0F, -1, centered = true)

        screenBetterProxy.proxy?.apply {
            FontWrapper.textShadow(matrices, socketAddress.address.hostAddress + ":" + socketAddress.port + " (" + ping + "ms)", 6F, 27F)
        }
    }

    inner class EntryScreenBetterSlotListEntryAccount(var account: Account) : EntryScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            logIn(account)
            super.onDoubleClickEntry(mouseX, mouseY, mouseButton)
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            super.renderEntry(matrices, index, entryWidth, entryHeight, mouseX, mouseY, hovered)

            FontWrapper.textShadow(matrices, Text.of(when {
                client?.session?.equals(account.session) == true -> Formatting.GREEN.toString()
                mainAccount == accounts.indexOf(account) -> Formatting.YELLOW.toString()
                else -> ""
            } + account.getDisplayName()).string, entryWidth / 2F, entryHeight / 4F + FontWrapper.fontHeight() / 4F, centered = true, scale = 2.0F)
        }
    }

    fun logIn(account: Account?) {
        if (account == null) return

        if (loginThread != null && loginThread?.isAlive!!) {
            try {
                (loginThread?.runnable as RunnableLogin).cancelled = true
            } catch (exception: IllegalStateException) { // This is an extremely tight case, which shouldn't happen in 99.9% of the cases
                status = Formatting.RED.toString() + exception.message
                return
            }
        }
        ThreadRunnableExposed(RunnableLogin(account)).also { loginThread = it }.start()
    }

    inner class RunnableLogin(private val account: Account) : Runnable {
        var cancelled = false
            set(value) {
                if (account.session != null)
                    error("Account has already been logged into")
                field = value
            }

        override fun run() {
            status = Formatting.YELLOW.toString() + "Logging in..."
            val prevAccount = currentAccount
            try {
                currentAccount = account
                account.logIn()
                if (cancelled)
                    return
                // This can't be "client" because it is called from ClientMain means it's null at this point in time
                var updatedUserApiService = true
                with(MinecraftClient.getInstance()) {
                    session = account.session
                    authenticationService = YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY, "", account.environment)
                    sessionService = account.getSessionService()
                    userApiService = try {
                        authenticationService.createUserApiService(account.session?.accessToken)
                    } catch (ignored: Exception) {
                        updatedUserApiService = false
                        UserApiService.OFFLINE
                    }
                    servicesSignatureVerifier = SignatureVerifier.create(authenticationService.servicesKey)
                    socialInteractionsManager = SocialInteractionsManager(MinecraftClient.getInstance(), userApiService)
                    (realmsPeriodicCheckers as IRealmsPeriodicCheckers).tarasande_getClient().apply {
                        username = account.session?.username
                        sessionId = account.session?.sessionId
                    }
                    profileKeys = ProfileKeys.create(userApiService, account.session, MinecraftClient.getInstance().runDirectory.toPath())

                    if (isMultiplayerBanned) {
                        setScreen(Bans.createBanScreen(object : BooleanConsumer {
                            override fun accept(confirmed: Boolean) {
                                if (confirmed) {
                                    Util.getOperatingSystem().open("https://aka.ms/mcjavamoderation")
                                }
                                setScreen(TitleScreen(true))
                            }
                        }, multiplayerBanDetails))
                    }
                }
                status = Formatting.GREEN.toString() + "Logged in as \"" + account.getDisplayName() + "\"" + if (!updatedUserApiService) Formatting.RED.toString() + " (failed to update UserApiService)" else ""

            } catch (e: Throwable) {
                e.printStackTrace()
                status = if (e.message?.isEmpty()!!) Formatting.RED.toString() + "Login failed!" else Formatting.RED.toString() + e.message
                currentAccount = prevAccount
            }
        }
    }
}
