package com.paragon.backend.bind

import com.paragon.backend.Feature

/**
 * @author aesthetical
 * @since 02/17/23
 */
fun interface BindInhibitor {
    fun act(feature: Feature, bind: Bind)
}