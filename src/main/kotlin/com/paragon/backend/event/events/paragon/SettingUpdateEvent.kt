package com.paragon.backend.event.events.paragon

import com.paragon.backend.setting.Setting
import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 11/02/2023
 */
class SettingUpdateEvent(val setting: Setting<*>) : Event() {

    override fun isCancellable() = true

}