package su.mandora.tarasande.feature.screen.accountmanager

import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.minecraft.UserApiService
import com.mojang.authlib.yggdrasil.ServicesKeyType
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.SocialInteractionsManager
import net.minecraft.client.session.ProfileKeys
import net.minecraft.client.session.Session
import net.minecraft.client.session.report.AbuseReportContext
import net.minecraft.client.session.report.ReporterEnvironment
import net.minecraft.client.session.telemetry.TelemetryManager
import net.minecraft.client.texture.PlayerSkinProvider
import net.minecraft.network.encryption.SignatureVerifier
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import org.apache.commons.lang3.RandomStringUtils
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.feature.screen.Screens.screenBetterProxy
import su.mandora.tarasande.feature.screen.accountmanager.file.FileAccounts
import su.mandora.tarasande.feature.screen.accountmanager.screenextension.ScreenExtensionButtonListMultiplayerScreen
import su.mandora.tarasande.feature.screen.accountmanager.subscreen.ScreenBetterAccount
import su.mandora.tarasande.injection.accessor.IMinecraftClient
import su.mandora.tarasande.injection.accessor.IRealmsPeriodicCheckers
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.filesystem.ManagerFile
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.impl.AccountSession
import su.mandora.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_HEIGHT
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH
import su.mandora.tarasande.util.MAX_NAME_LENGTH
import su.mandora.tarasande.util.extension.javaruntime.Thread
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.screen.EntryScreenBetterSlotList
import su.mandora.tarasande.util.screen.ScreenBetterSlotList
import java.util.*
import java.util.concurrent.CompletableFuture
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

    init {
        ManagerScreenExtension.add(ScreenExtensionButtonListMultiplayerScreen())
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

        val upperRow = generateButtonRow(3, DEFAULT_BUTTON_WIDTH, BUTTON_PADDING)

        addDrawableChild(ButtonWidget(width / 2 + upperRow[0], height - 46 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Login")) {
            logIn(this.selected() ?: return@ButtonWidget)
        }.also { loginButton = it })
        addDrawableChild(ButtonWidget(width / 2 + upperRow[1], height - 46 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Remove")) {
            val account = this.selected() ?: return@ButtonWidget

            if (accounts.indexOf(account) == mainAccount) mainAccount = null
            accounts.remove(account)
            reload()
        }.also { removeButton = it })
        addDrawableChild(ButtonWidget(width / 2 + upperRow[2], height - 46 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Toggle main")) {
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

        addDrawableChild(ButtonWidget(width / 2 + lowerRow[0], height - 46 + 2 + 20 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Update session")) {
            val account = this.selected() ?: return@ButtonWidget
            updateSession(account)
        }.also { updateSessionButton = it })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[1], height - 46 + 2 + 20 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Direct Login")) { client?.setScreen(ScreenBetterAccount("Direct Login", this) { logIn(it) }) })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[2], height - 46 + 2 + 20 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Random Account")) { accounts.randomOrNull()?.also { logIn(it) } }.also { randomButton = it })
        addDrawableChild(ButtonWidget(width / 2 + lowerRow[3], height - 46 + 2 + 20 - 3, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Add")) {
            client?.setScreen(ScreenBetterAccount("Add Account", this) { account ->
                accounts.add(account)
                reload()
            })
        }.also { addButton = it })

        addDrawableChild(ButtonWidget(BUTTON_PADDING, BUTTON_PADDING, (DEFAULT_BUTTON_WIDTH * 1.5).toInt() /* wide button */, DEFAULT_BUTTON_HEIGHT, Text.of("Add random session")) {
            accounts.add(AccountSession().also {
                it.username = RandomStringUtils.randomAlphanumeric(MAX_NAME_LENGTH)
                it.uuid = UUID.randomUUID().toString()
            })
            reload()
        })

        addDrawableChild(ButtonWidget(width - BUTTON_PADDING - DEFAULT_BUTTON_WIDTH, BUTTON_PADDING, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Proxy")) {
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

            account.skin?.drawHead(context, 5, 5, 32)

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
            return@with try {
                val args = (this as IMinecraftClient).tarasande_getRunArgs()

                authenticationService = account.yggdrasilAuthenticationService
                sessionService = account.minecraftSessionService
                session = account.session
                SignatureVerifier.create(authenticationService.servicesKeySet, ServicesKeyType.PROFILE_KEY)
                gameProfileFuture = CompletableFuture.supplyAsync(
                    { sessionService.fetchProfile(session.uuidOrNull, true) }, Util.getIoWorkerExecutor()
                )
                userApiService = try {
                    authenticationService.createUserApiService(session.accessToken)
                } catch (e: AuthenticationException) {
                    UserApiService.OFFLINE
                }
                skinProvider = PlayerSkinProvider(textureManager, args.directories.assetDir.toPath().resolve("skins"), sessionService, this)
                socialInteractionsManager = SocialInteractionsManager(this, userApiService)
                (realmsPeriodicCheckers as IRealmsPeriodicCheckers).tarasande_getClient().apply {
                    username = session.username
                    sessionId = session.sessionId
                }
                telemetryManager = TelemetryManager(this, userApiService, session)
                profileKeys = ProfileKeys.create(userApiService, session, args.directories.runDir.toPath())
                abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), userApiService)

                true
            } catch (t: Throwable) {
                t.printStackTrace()
                false
            }
        }
        currentAccount = account
        if(success || mc.userApiService == UserApiService.OFFLINE)
            account.status = Formatting.GREEN.toString() + "Updated session"
        else
            account.status = Formatting.RED.toString() + "Failed to update session"
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
