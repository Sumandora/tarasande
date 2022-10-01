package net.tarasandedevelopment.tarasande.screen.list.accountmanager

import com.mojang.authlib.minecraft.UserApiService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.SocialInteractionsManager
import net.minecraft.client.util.ProfileKeys
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.encryption.SignatureVerifier
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.environment.ManagerEnvironment
import net.tarasandedevelopment.tarasande.event.EventSessionService
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.screen.list.accountmanager.subscreens.ScreenBetterAccount
import net.tarasandedevelopment.tarasande.util.render.screen.ScreenBetter
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

class ScreenBetterAccountManager : ScreenBetter(null) {

    val accounts = ArrayList<Account>()
    var currentAccount: Account? = null

    var mainAccount: Int? = null

    var loginThread: ThreadRunnableExposed? = null
    var status: String? = null

    private var accountList: AlwaysSelectedEntryListWidgetAccount? = null

    private var loginButton: ButtonWidget? = null
    private var removeButton: ButtonWidget? = null
    private var setMainButton: ButtonWidget? = null
    private var addButton: ButtonWidget? = null
    private var randomButton: ButtonWidget? = null

    val managerAccount = ManagerAccount()
    val managerEnvironment = ManagerEnvironment()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventSessionService) {
                event.sessionService = currentAccount?.getSessionService() ?: return@add
            }
        }
    }

    override fun init() {
        addDrawableChild(AlwaysSelectedEntryListWidgetAccount(client, width, height, 16, height - 46).also { accountList = it })

        addDrawableChild(ButtonWidget(width / 2 - 203, height - 46 + 2, 100, 20, Text.of("Login")) { logIn(accountList?.selectedOrNull?.account!!) }.also { loginButton = it })
        addDrawableChild(ButtonWidget(width / 2 - 101, height - 46 + 2, 100, 20, Text.of("Remove")) {
            if (accounts.indexOf(accountList?.selectedOrNull?.account) == mainAccount) mainAccount = null
            accounts.remove(accountList?.selectedOrNull?.account)
            accountList?.reload()
            accountList?.setSelected(null)
        }.also { removeButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 1, height - 46 + 2, 100, 20, Text.of("Direct Login")) {
            client?.setScreen(ScreenBetterAccount(this, "Direct Login") {
                logIn(it)
            })
        }.also { addButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 103, height - 46 + 2, 100, 20, Text.of("Set Main")) {
            val account = accountList?.selectedOrNull?.account!!
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
        }.also { setMainButton = it })

        addDrawableChild(ButtonWidget(width / 2 - 203, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount(this, "Direct Login") { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 - 101, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Random Account")) { logIn(accounts[ThreadLocalRandom.current().nextInt(accounts.size)]) }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 1, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount(this, "Add Account") { account ->
                accounts.add(account)
                accountList?.reload()
            })
        }.also { addButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 103, height - 46 + 2 + 20 + 2, 100, 20, Text.of("Back")) {
            RenderSystem.recordRenderCall {
                close()
            }
        })

        tick()
        super.init()
    }

    override fun tick() {
        loginButton?.active = accountList?.selectedOrNull != null
        removeButton?.active = accountList?.selectedOrNull != null
        setMainButton?.active = accountList?.selectedOrNull != null
        if (accountList?.selectedOrNull != null) setMainButton?.active = accountList?.selectedOrNull?.account?.isSuitableAsMain() == true
        randomButton?.active = accounts.isNotEmpty()
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, if (status == null) "Account Manager" else status, width / 2, 8 - textRenderer.fontHeight / 2, -1)
    }

    override fun close() {
        status = null
        super.close()
    }

    inner class AlwaysSelectedEntryListWidgetAccount(mcIn: MinecraftClient?, widthIn: Int, heightIn: Int, topIn: Int, bottomIn: Int) : AlwaysSelectedEntryListWidget<AlwaysSelectedEntryListWidgetAccount.EntryAccount>(mcIn, widthIn, heightIn, topIn, bottomIn, MinecraftClient.getInstance().textRenderer.fontHeight * 2) {
        internal fun reload() {
            this.clearEntries()
            for (account in accounts) {
                this.addEntry(EntryAccount(account))
            }
        }

        override fun getScrollbarPositionX() = width - 6 // sick hardcoded value, thx mojang

        init {
            reload()
        }

        inner class EntryAccount(var account: Account) : Entry<EntryAccount>() {
            private var lastClick: Long = 0

            override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
                if (button == 0) {
                    if (System.currentTimeMillis() - lastClick < 300) {
                        logIn(account)
                    }
                    setSelected(this)
                    lastClick = System.currentTimeMillis()
                }
                return super.mouseClicked(x, y, button)
            }

            override fun render(matrices: MatrixStack?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
                matrices?.push()
                matrices?.translate((width / 2f).toDouble(), (y + textRenderer.fontHeight - textRenderer.fontHeight / 2f).toDouble(), 0.0)
                matrices?.scale(2.0f, 2.0f, 1.0f)
                matrices?.translate(-(width / 2f).toDouble(), (-(y + textRenderer.fontHeight - textRenderer.fontHeight / 2f)).toDouble(), 0.0)
                drawCenteredText(matrices, textRenderer, Text.of(when {
                    client?.session?.equals(account.session) == true -> Formatting.GREEN.toString()
                    mainAccount == accounts.indexOf(account) -> Formatting.YELLOW.toString()
                    else -> ""
                } + account.getDisplayName()), width / 2, y + 2, Color.white.rgb)
                matrices?.pop()
            }

            override fun getNarration(): Text = Text.of(account.getDisplayName())
        }
    }

    fun logIn(account: Account) {
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

    inner class RunnableLogin(var account: Account) : Runnable {
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
                (MinecraftClient.getInstance() as IMinecraftClient).also {
                    it.tarasande_setSession(account.session)
                    val authenticationService = YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY, "", account.environment)
                    it.tarasande_setAuthenticationService(authenticationService)
                    val userApiService = try {
                        authenticationService.createUserApiService(account.session?.accessToken)
                    } catch (ignored: Exception) {
                        updatedUserApiService = false
                        UserApiService.OFFLINE
                    }
                    it.tarasande_setUserApiService(userApiService)
                    it.tarasande_setSessionService(account.getSessionService())
                    it.tarasande_setServicesSignatureVerifier(SignatureVerifier.create(authenticationService.servicesKey))
                    it.tarasande_setSocialInteractionsManager(SocialInteractionsManager(MinecraftClient.getInstance(), userApiService))
                    it.tarasande_setProfileKeys(ProfileKeys(it.tarasande_getUserApiService(), account.session?.profile?.id, MinecraftClient.getInstance().runDirectory.toPath()))
                }
                status = Formatting.GREEN.toString() + "Logged in as \"" + account.getDisplayName() + "\""

                if (!updatedUserApiService)
                    status += Formatting.RED.toString() + " (failed to update UserApiService)"
            } catch (e: Throwable) {
                e.printStackTrace()
                status = if (e.message?.isEmpty()!!) Formatting.RED.toString() + "Login failed!" else Formatting.RED.toString() + e.message
                currentAccount = prevAccount
            }
        }
    }
}
