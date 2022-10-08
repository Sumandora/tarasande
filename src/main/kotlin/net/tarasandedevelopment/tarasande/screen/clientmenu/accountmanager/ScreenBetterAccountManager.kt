package net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager

import com.mojang.authlib.minecraft.UserApiService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.SocialInteractionsManager
import net.minecraft.client.util.ProfileKeys
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.encryption.SignatureVerifier
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.azureapp.ManagerAzureApp
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.environment.ManagerEnvironment
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.subscreens.ScreenBetterAccount
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

class ScreenBetterAccountManager : ScreenBetterSlotList(46, 10, MinecraftClient.getInstance().textRenderer.fontHeight * 2) {

    val accounts = ArrayList<Account>()
    var currentAccount: Account? = null

    var mainAccount: Int? = null

    var loginThread: ThreadRunnableExposed? = null

    private var loginButton: ButtonWidget? = null
    private var removeButton: ButtonWidget? = null
    private var setMainButton: ButtonWidget? = null
    private var addButton: ButtonWidget? = null
    private var randomButton: ButtonWidget? = null

    val managerAccount = ManagerAccount()
    val managerEnvironment = ManagerEnvironment()
    val managerAzureApp = ManagerAzureApp()

    var status = ""

    fun selected(): Account? {
        if (this.slotList!!.selectedOrNull == null) return null

        return (this.slotList!!.selectedOrNull as EntryAccount).account
    }

    override fun init() {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return accounts.map { a -> EntryAccount(a) }
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
        addDrawableChild(ButtonWidget(width / 2 + 52, height - 46 - 3, 100, 20, Text.of("Set Main")) {
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
        }.also { setMainButton = it })

        addDrawableChild(ButtonWidget(width / 2 - 152, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount(this, "Direct Login") { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 - 50, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Random Account")) { logIn(accounts[ThreadLocalRandom.current().nextInt(accounts.size)]) }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + 52, height - 46 + 2 + 20 - 3, 100, 20, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount(this, "Add Account") { account ->
                accounts.add(account)
                this.slotList?.reload()
            })
        }.also { addButton = it })
        addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            RenderSystem.recordRenderCall {
                close()
            }
        })
        tick()
    }

    override fun tick() {
        loginButton?.active = slotList?.selectedOrNull != null
        removeButton?.active = slotList?.selectedOrNull != null
        setMainButton?.active = slotList?.selectedOrNull != null
        if (slotList?.selectedOrNull != null) setMainButton?.active = true
        randomButton?.active = accounts.isNotEmpty()
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        this.renderTitle(matrices, "Account Manager")
        drawCenteredText(matrices, textRenderer, Text.of(status), width / 2, 2 * textRenderer.fontHeight * 2, Color.white.rgb)
    }

    inner class EntryAccount(var account: Account) : ScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            logIn(account)
            super.onDoubleClickEntry(mouseX, mouseY, mouseButton)
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            super.renderEntry(matrices, index, entryWidth, entryHeight, mouseX, mouseY, hovered)

            matrices.push()
            matrices.translate((entryWidth).toDouble(), (textRenderer.fontHeight - textRenderer.fontHeight / 2f).toDouble(), 0.0)
            matrices.scale(2.0f, 2.0f, 1.0f)
            matrices.translate(-(entryWidth).toDouble(), (-(textRenderer.fontHeight - textRenderer.fontHeight / 2f)).toDouble(), 0.0)
            drawCenteredText(matrices, textRenderer, Text.of(when {
                client?.session?.equals(account.session) == true -> Formatting.GREEN.toString()
                mainAccount == accounts.indexOf(account) -> Formatting.YELLOW.toString()
                else -> ""
            } + account.getDisplayName()), entryWidth, 2, Color.white.rgb)
            matrices.pop()
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
                status = Formatting.GREEN.toString() + "Logged in as \"" + account.getDisplayName() + "\"" + if (!updatedUserApiService) Formatting.RED.toString() + " (failed to update UserApiService)" else ""

            } catch (e: Throwable) {
                e.printStackTrace()
                status = if (e.message?.isEmpty()!!) Formatting.RED.toString() + "Login failed!" else Formatting.RED.toString() + e.message
                currentAccount = prevAccount
            }
        }
    }
}
