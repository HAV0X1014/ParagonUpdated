package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.event.events.render.RenderHUDEvent
import com.paragon.client.toasts.Toast
import com.paragon.client.toasts.ToastType
import com.paragon.util.rendering.NVGWrapper
import me.bush.eventbus.annotation.EventListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author aesthetical
 * @since 02/19/23
 */
class ToastManager {

    private val toastMap: MutableMap<Int, Toast> = ConcurrentHashMap()
    private val ID = AtomicInteger(0)

    init {
        Paragon.bus.subscribe(this)
    }

    @EventListener
    fun onRenderHUD(event: RenderHUDEvent) {
        NVGWrapper.scope { nvg ->
            if (toastMap.isEmpty()) {
                return@scope
            }

            var posY = event.height - 56.0f

            for (id in toastMap.keys) {
                val toast = toastMap[id]

                if (toast == null || toast.isDead) {
                    toastMap.remove(id)
                    continue
                }

                toast.updateAndRender(nvg, posY, event.width)
                posY -= (56.0f * toast.animation.animationFactor.toFloat())
            }
        }
    }

    fun info(deployer: String, content: String, lifespan: Long) {
        add(ID.getAndIncrement(), Toast(ToastType.INFO, deployer, content, lifespan))
    }

    fun info(id: Int, deployer: String, content: String, lifespan: Long) {
        add(id, Toast(ToastType.INFO, deployer, content, lifespan))
    }

    fun warn(deployer: String, content: String, lifespan: Long) {
        add(ID.getAndIncrement(), Toast(ToastType.WARNING, deployer, content, lifespan))
    }

    fun warn(id: Int, deployer: String, content: String, lifespan: Long) {
        add(id, Toast(ToastType.WARNING, content, deployer, lifespan))
    }

    private fun add(id: Int, toast: Toast) {
        if (!toastMap.containsKey(id)) {
            toastMap[id] = toast
        } else {
            val t = toastMap[id]
            t!!.content = toast.content
            t.endTime = System.currentTimeMillis() + t.lifespan
        }
    }

}