package com.paragon.backend

import com.paragon.backend.setting.SettingContainer

/**
 * @author surge
 * @since 11/02/2023
 */
open class Feature(val name: String, val description: String) : SettingContainer() {

    open fun postLoad() {}

}