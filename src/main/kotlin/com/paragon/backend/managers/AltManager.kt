package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.alt.Alt
import com.paragon.backend.config.Config
import com.paragon.mixin.duck.IMinecraftClient
import com.paragon.util.backgroundThread
import com.paragon.util.io.FileUtil
import com.paragon.util.mc
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator
import net.minecraft.client.util.Session
import org.json.JSONObject
import java.util.*

/**
 * @author surge
 * @since 22/02/2023
 */
class AltManager {

    private val microshitAuth = MicrosoftAuthenticator()
    val alts = mutableListOf<Alt>()

    var status = "Logged in as ${mc.session.username}"

    init {

        object : Config("alts.json") {
            override fun save() {
                val json = JSONObject()

                alts.forEach {
                    val altJson = JSONObject()
                    altJson.put("password", it.password)
                    altJson.put("cachedUsername", it.cachedUsername)

                    if (it.accountType == Session.AccountType.MSA && !it.refreshToken.isNullOrEmpty()) {
                        // TODO: don't store in plaintext
                        altJson.put("refreshToken", it.refreshToken)
                    }

                    json.put(it.email, altJson)
                }

                if (!file.exists()) {
                    file.createNewFile()
                }

                FileUtil.write(file, json.toString(4))
            }

            override fun load() {
                if (!file.exists()) {
                    return
                }

                val content = FileUtil.read(file)
                if (content.isNullOrEmpty()) {
                    return
                }

                try {
                    val json = JSONObject(content)

                    json.keySet().forEach {
                        val altJson = json.getJSONObject(it)

                        val alt = Alt(it, altJson.getString("password"))
                        if (altJson.has("cachedUsername")) {
                            alt.cachedUsername = altJson.getString("cachedUsername")
                        }

                        if (altJson.has("refreshToken") && alt.accountType == Session.AccountType.MSA) {
                            alt.refreshToken = altJson.getString("refreshToken")
                        }

                        alts.add(alt)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    fun login(alt: Alt) {

        // run in a different thread to not freeze up the main thread
        backgroundThread {
            when (alt.accountType) {
                Session.AccountType.MSA -> {
                    if (alt.session == null) {
                        Paragon.logger.info("Logging in with ${alt.email}")
                        status = "Logging in with ${alt.cachedUsername}"

                        if (alt.refreshToken.isNullOrEmpty()) {
                            try {
                                val result = microshitAuth.loginWithCredentials(alt.email, alt.password) // Get auth result

                                // Set alt session
                                alt.refreshToken = result.refreshToken
                                alt.session = Session(result.profile.name, result.profile.id, result.accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MSA)

                                status = "Logged in as ${alt.cachedUsername}"
                            } catch (e: MicrosoftAuthenticationException) {
                                status = "Couldn't log in as ${alt.cachedUsername}"
                                e.printStackTrace()
                                return@backgroundThread
                            }
                        } else {
                            if (!loginWithRefreshToken(alt)) {
                                // re-login
                                Paragon.logger.info("Failed to use refresh token. Re-logging in")
                                alt.refreshToken = null
                                login(alt)

                                return@backgroundThread
                            }
                        }

                    }
                }

                Session.AccountType.LEGACY -> {
                    alt.session = Session(alt.email, UUID.randomUUID().toString(), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY)
                    status = "Logged in as ${alt.cachedUsername}"
                    return@backgroundThread
                }

                else -> {
                    Paragon.logger.warn("Unknown state with alt ${alt.email}")
                    status = "Unknown state with ${alt.cachedUsername}"
                    return@backgroundThread
                }
            }
        }.invokeOnCompletion {
            alt.cachedUsername = alt.session!!.username
            (mc as IMinecraftClient).setSession(alt.session)
        }
    }

    private fun loginWithRefreshToken(alt: Alt): Boolean {
        if (alt.accountType != Session.AccountType.MSA || alt.refreshToken.isNullOrEmpty()) {
            login(alt)
            return false
        }

        return try {
            val result = microshitAuth.loginWithRefreshToken(alt.refreshToken)

            // Set alt session
            alt.session = Session(result.profile.name, result.profile.id, result.accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MSA)
            status = "Logged in as ${alt.cachedUsername}"

            true
        } catch (e: MicrosoftAuthenticationException) {
            status = "Failed to log in as ${alt.cachedUsername}"
            e.printStackTrace()
            false
        }
    }
}