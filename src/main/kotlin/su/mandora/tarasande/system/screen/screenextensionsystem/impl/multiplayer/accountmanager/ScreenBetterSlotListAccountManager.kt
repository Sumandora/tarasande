package su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.SocialInteractionsManager
import net.minecraft.client.report.AbuseReportContext
import net.minecraft.client.report.ReporterEnvironment
import net.minecraft.client.texture.PlayerSkinProvider
import net.minecraft.client.util.ProfileKeys
import net.minecraft.client.util.Session
import net.minecraft.client.util.telemetry.TelemetryManager
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.apache.commons.lang3.RandomStringUtils
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.injection.accessor.IMinecraftClient
import su.mandora.tarasande.injection.accessor.IRealmsPeriodicCheckers
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.filesystem.ManagerFile
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.impl.AccountSession
import su.mandora.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.file.FileAccounts
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterAccount
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterProxy
import su.mandora.tarasande.util.extension.javaruntime.Thread
import su.mandora.tarasande.util.extension.minecraft.ButtonWidget
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.screen.EntryScreenBetterSlotList
import su.mandora.tarasande.util.screen.ScreenBetterSlotList
import java.io.File
import java.util.*
import kotlin.math.max

class ScreenBetterSlotListAccountManager : ScreenBetterSlotList("Account Manager", null /*set else where*/, 46, 10) {

    val accounts = ArrayList<Account>()
    var currentAccount: Account? = null

    var mainAccount: Int? = null

    private lateinit var updateSessionButton: ButtonWidget
    private lateinit var loginButton: ButtonWidget
    private lateinit var removeButton: ButtonWidget
    private lateinit var toggleMainButton: ButtonWidget
    private lateinit var addButton: ButtonWidget
    private lateinit var randomButton: ButtonWidget

    init {
        ManagerAzureApp
        ManagerAccount
    }

    val screenBetterProxy = ScreenBetterProxy()

    init {
        EventDispatcher.add(EventSuccessfulLoad::class.java, 9999) {
            ManagerFile.add(FileAccounts(this))

            if (mc.session?.accountType == Session.AccountType.LEGACY && mainAccount != null) {
                val account = accounts[mainAccount!!]
                try {
                    val thread = logIn(account)

                    while (thread.isAlive)
                        Thread.sleep(50L) // synchronize

                    updateSession(account)
                } catch (t: Throwable) {
                    account.status = t.message
                }
            }
        }
    }

    private fun selected(): Account? {
        return (this.slotList?.selectedOrNull as? EntryScreenBetterSlotListAccount)?.account
    }

    private fun generateButtonRow(buttons: Int, width: Int, padding: Int): IntArray {
        var pos = -(width + padding) * buttons / 2.0
        val ints = IntArray(buttons)

        for (int in ints.indices) {
            ints[int] = pos.toInt()
            pos += width + padding
        }

        return ints
    }

    override fun init() {
        this.provideElements {
            accounts.map { EntryScreenBetterSlotListAccount(it) }
        }
        super.init()

        val upperRow = generateButtonRow(3, 100, 3)

        addDrawableChild(ButtonWidget(width / 2 + upperRow[0], height - 46 - 3, 100, 20, Text.of("Login")) {
            logIn(this.selected() ?: return@ButtonWidget)
        }.also { loginButton = it })
        addDrawableChild(ButtonWidget(width / 2 + upperRow[1], height - 46 - 3, 100, 20, Text.of("Remove")) {
            val account = this.selected() ?: return@ButtonWidget

            if (accounts.indexOf(account) == mainAccount) mainAccount = null
            accounts.remove(account)
            reload()
        }.also { removeButton = it })
        addDrawableChild(ButtonWidget(width / 2 + upperRow[2], height - 46 - 3, 100, 20, Text.of("Toggle main")) {
            val account = this.selected() ?: return@ButtonWidget

            if (account.session == null) {
                account.status = Formatting.RED.toString() + "Account hasn't been logged into yet"
            } else {
                val index = accounts.indexOf(account)
                if (mainAccount != index) {
                    mainAccount = index
                    account.status = Formatting.YELLOW.toString() + account.getDisplayName() + " is now the Main-Account"
                } else {
                    mainAccount = null
                    account.status = Formatting.YELLOW.toString() + account.getDisplayName() + " is no longer a Main-Account"
                }
            }
        }.also { toggleMainButton = it })

        val lowerRow = generateButtonRow(4, 100, 3)

        addDrawableChild(ButtonWidget(width / 2 + lowerRow[0], height - 46 + 2 + 20 - 3, 100, 20, Text.of("Update session")) {
            val account = this.selected() ?: return@ButtonWidget
            updateSession(account)
        }.also { updateSessionButton = it })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[1], height - 46 + 2 + 20 - 3, 100, 20, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount("Direct Login", this) { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[2], height - 46 + 2 + 20 - 3, 100, 20, Text.of("Random Account")) { accounts.randomOrNull()?.also { logIn(it) } }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[3], height - 46 + 2 + 20 - 3, 100, 20, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount("Add Account", this) { account ->
                accounts.add(account)
                reload()
            })
        }.also { addButton = it })

        addDrawableChild(ButtonWidget(3, 3, 150, 20, Text.of("Add random session")) {
            accounts.add(AccountSession().also {
                it.username = RandomStringUtils.randomAlphanumeric(16)
                it.uuid = UUID.randomUUID().toString()
            })
            reload()
        })

        addDrawableChild(ButtonWidget(width - 103, 3, 100, 20, Text.of("Proxy")) {
            mc.setScreen(screenBetterProxy.apply { prevScreen = mc.currentScreen })
        })

        tick()
    }

    override fun tick() {
        val slotList = slotList // Allow smart cast
        if (slotList != null) {
            loginButton.active = slotList.selectedOrNull != null
            removeButton.active = slotList.selectedOrNull != null
            toggleMainButton.active = slotList.selectedOrNull != null
        }
        updateSessionButton.active = selected()?.ready() == true
        randomButton.active = accounts.isNotEmpty()
        super.tick()
    }

    inner class EntryScreenBetterSlotListAccount(var account: Account) : EntryScreenBetterSlotList(max(320, (FontWrapper.getWidth(account.getDisplayName()) + 5) * 2), FontWrapper.fontHeight() * 5) {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            if (account.ready())
                updateSession(account)
            else
                logIn(account)
        }

        override fun getNarration(): Text = Text.of(account.getDisplayName())

        override fun renderEntry(context: DrawContext, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            val formatting = when {
                currentAccount == account -> Formatting.GREEN
                mainAccount == accounts.indexOf(account) -> Formatting.YELLOW
                else -> Formatting.RESET
            }
            FontWrapper.textShadow(context, formatting.toString() + account.getDisplayName(), entryWidth / 2F, entryHeight / 2F - FontWrapper.fontHeight(), centered = true, scale = 2F)
            if (account.status != null)
                FontWrapper.textShadow(context, account.status!!, entryWidth / 2F, entryHeight / 2F + FontWrapper.fontHeight(), centered = true)
            if (account.skin != null)
                account.skin!!.draw(context, 5, 5, 32)

            val accountInfoName = account.javaClass.getAnnotation(AccountInfo::class.java).name
            FontWrapper.text(context, accountInfoName, entryWidth - FontWrapper.getWidth(accountInfoName).toFloat() - 7F, 5F)
        }
    }

    private fun logIn(account: Account): Thread {
        return Thread("$TARASANDE_NAME account login thread", RunnableLogin(account)).also {
            it.start()
        }
    }

    private fun updateSession(account: Account) {
        if (!account.ready())
            return

        // This can't be "client" because it is called from ClientMain means it's null at this point in time
        val success = with(mc) {
            session = account.session

            try {
                authenticationService = YggdrasilAuthenticationService(networkProxy)
                sessionService = account.service
                skinProvider = PlayerSkinProvider(textureManager, File((this as IMinecraftClient).tarasande_getRunArgs().directories.assetDir, "skins"), sessionService)
                (realmsPeriodicCheckers as IRealmsPeriodicCheckers).tarasande_getClient().apply {
                    username = account.session?.username
                    sessionId = account.session?.sessionId
                }
                userApiService = authenticationService.createUserApiService(session.accessToken)
                socialInteractionsManager = SocialInteractionsManager(this, userApiService)
                telemetryManager = TelemetryManager(this, userApiService, session)
                profileKeys = ProfileKeys.create(userApiService, session, this.runDirectory.toPath())
                abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), userApiService)
                return@with true
            } catch (t: Throwable) {
                t.printStackTrace()
                return@with false
            }
        }
        currentAccount = account
        account.status = Formatting.GREEN.toString() + "Updated session" + if (!success) Formatting.RED.toString() + " (failed to update UserApiService)" else ""
    }

    inner class RunnableLogin(private val account: Account) : Runnable {

        override fun run() {
            account.status = Formatting.YELLOW.toString() + "Logging in..."
            try {
                account.logIn()
                account.status = Formatting.GREEN.toString() + "The account has been logged into."
            } catch (e: Throwable) {
                e.printStackTrace()
                account.status = Formatting.RED.toString() + e.localizedMessage.ifEmpty { "Login failed!" }
            }
        }
    }
}
