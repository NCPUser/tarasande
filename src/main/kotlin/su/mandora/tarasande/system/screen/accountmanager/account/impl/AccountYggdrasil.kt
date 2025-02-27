package su.mandora.tarasande.system.screen.accountmanager.account.impl

import com.google.gson.JsonArray
import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.util.Session
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import java.util.*

@AccountInfo(name = "Yggdrasil")
class AccountYggdrasil : Account() {

    @TextFieldInfo("Username/E-Mail", false)
    var username = ""

    @TextFieldInfo("Password", true)
    var password = ""

    override fun logIn() {
        yggdrasilAuthenticationService = YggdrasilAuthenticationService(mc.networkProxy, "", environment)
        val userAuthentication = YggdrasilUserAuthentication(yggdrasilAuthenticationService, "", Agent.MINECRAFT, environment)
        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)
        userAuthentication.logIn()
        if (userAuthentication.isLoggedIn) {
            session = Session(userAuthentication.selectedProfile.name, userAuthentication.selectedProfile.id.toString(), userAuthentication.authenticatedToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG)
            minecraftSessionService = yggdrasilAuthenticationService!!.createMinecraftSessionService()
        }
    }

    override fun getDisplayName() = if (session != null) session?.username!! else username

    override fun save(): JsonArray {
        val jsonArray = JsonArray()
        jsonArray.add(username)
        jsonArray.add(password)
        return jsonArray
    }

    override fun load(jsonArray: JsonArray): Account {
        val account = AccountYggdrasil()
        account.username = jsonArray[0].asString
        account.password = jsonArray[1].asString

        return account
    }

    override fun create(credentials: List<String>) {
        username = credentials[0]
        password = credentials[1]
    }
}