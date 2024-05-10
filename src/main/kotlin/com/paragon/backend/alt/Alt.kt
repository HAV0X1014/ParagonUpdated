package com.paragon.backend.alt

import net.minecraft.client.util.Session
import net.minecraft.client.util.Session.AccountType

/**
 * @author surge
 * @since 02/22/23
 */
data class Alt(val email: String, val password: String?) {

    var accountType: AccountType = AccountType.MSA

    init {
        if (password.isNullOrEmpty()) {
            accountType = AccountType.LEGACY
        }
    }

    var refreshToken: String? = null
        set(value) {
            if (accountType != AccountType.MSA) {
                throw IllegalStateException("Cannot set refresh token for a non MSA account")
            }

            field = value
        }

    var session: Session? = null
    var cachedUsername = email

}
