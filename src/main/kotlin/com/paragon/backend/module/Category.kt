package com.paragon.backend.module

/**
 * @author surge
 * @since 11/02/2023
 */
enum class Category(val displayName: String) {
    /**
     * Combat modules, e.g. Aura
     */
    COMBAT("Combat"),

    /**
     * Exploit modules, e.g. Ping Spoof, Packet Flight
     */
    EXPLOIT("Exploit"),

    /**
     * Movement modules, e.g. Step
     */
    MOVEMENT("Movement"),

    /**
     * Visual modules, e.g. ESP
     */
    VISUAL("Visual"),

    /**
     * Player modules, e.g. Anti Void
     */
    PLAYER("Player")
}