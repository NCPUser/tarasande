package su.mandora.tarasande

import com.google.gson.GsonBuilder
import de.enzaxd.viaforge.ViaForge
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.util.Util
import org.slf4j.LoggerFactory
import su.mandora.tarasande.base.esp.ManagerESP
import su.mandora.tarasande.base.event.ManagerEvent
import su.mandora.tarasande.base.file.ManagerFile
import su.mandora.tarasande.base.module.ManagerModule
import su.mandora.tarasande.base.util.player.clickspeed.ManagerClickMethod
import su.mandora.tarasande.base.value.ManagerValue
import su.mandora.tarasande.screen.Screens
import su.mandora.tarasande.util.clientvalue.ClientValues
import su.mandora.tarasande.util.player.entitycolor.EntityColor
import su.mandora.tarasande.util.player.friends.Friends
import su.mandora.tarasande.util.render.blur.Blur

class TarasandeMain {

    val name = "tarasande" // "lowercase gang" ~kennytv

    lateinit var managerEvent: ManagerEvent
        private set
    private lateinit var managerFile: ManagerFile
    lateinit var managerValue: ManagerValue
        private set
    lateinit var clientValues: ClientValues
        private set
    lateinit var protocolHack: ViaForge
        private set
    lateinit var entityColor: EntityColor
        private set
    lateinit var managerClickMethod: ManagerClickMethod
        private set
    lateinit var managerModule: ManagerModule
        private set
    lateinit var blur: Blur
        private set
    lateinit var screens: Screens
        private set
    lateinit var friends: Friends
        private set
    lateinit var managerESP: ManagerESP
        private set

    val logger = LoggerFactory.getLogger(name)!!

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()!!

    val autoSaveDaemonName = "$name config auto save daemon"
    val autoSaveDaemon: Thread /* This has to be here because IntelliJ is the best IDE ever built */ = Thread({
        while (true) {
            if (clientValues.autoSaveConfig.value) {
                managerFile.save()
                Thread.sleep(clientValues.autoSaveDelay.value.toLong())
            }
        }
    }, autoSaveDaemonName)

    val linux = Util.getOperatingSystem() != Util.OperatingSystem.WINDOWS

    companion object {
        private val instance: TarasandeMain = TarasandeMain()

        fun get(): TarasandeMain {
            return instance
        }
    }

    fun onPreLoad() {
        managerEvent = ManagerEvent()
    }

    fun onLateLoad() {
        managerFile = ManagerFile()
        managerValue = ManagerValue()
        clientValues = ClientValues()
        entityColor = EntityColor()
        managerClickMethod = ManagerClickMethod()
        managerModule = ManagerModule()
        managerESP = ManagerESP()
        blur = Blur()
        screens = Screens() // Initializes ClickGUI (Make sure that modules, values, blur etc... is initialized before)
        friends = Friends()

        protocolHack = ViaForge()

        managerFile.load()

        if (MinecraftClient.getInstance().session?.accountType == Session.AccountType.LEGACY && screens.screenBetterAccountManager.mainAccount != null) {
            screens.screenBetterAccountManager.logIn(screens.screenBetterAccountManager.accounts[screens.screenBetterAccountManager.mainAccount!!])
            while (screens.screenBetterAccountManager.loginThread != null && screens.screenBetterAccountManager.loginThread!!.isAlive)
                Thread.sleep(50L) // synchronize
            screens.screenBetterAccountManager.status = null
        }

        autoSaveDaemon.start()
        // We can't guarantee that qdbus exists, nor can we guarantee that we are even using kde plasma, just hope for the best ^^
        if (linux) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor suspend")
            } catch (ignored: Throwable) {
            }
        }
    }

    fun onUnload() {
        if (linux) {
            try {
                Runtime.getRuntime().exec("qdbus org.kde.KWin /Compositor resume")
            } catch (ignored: Throwable) {
            }
        }

        managerFile.save()
    }
}