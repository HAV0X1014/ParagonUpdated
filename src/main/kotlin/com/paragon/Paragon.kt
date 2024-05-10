package com.paragon

import com.paragon.backend.Feature
import com.paragon.backend.event.EventProcessor
import com.paragon.backend.managers.*
import com.paragon.backend.managers.placement.PlacementManager
import com.paragon.backend.module.Module
import com.paragon.client.modules.visual.ClickGUI
import com.paragon.client.ui.configuration.aesthetical.AestheticalUI
import com.paragon.client.ui.configuration.surge.SurgeUI
import com.paragon.util.BuildConfig.BuildConfig
import com.paragon.util.io.FileUtil
import me.bush.eventbus.bus.EventBus
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory
import kotlin.math.log

/**
 * @author surge
 * @since 19/02/2023
 */
class Paragon : ClientModInitializer {

    override fun onInitializeClient() {
        logger.info("Initialising Paragon")

        if (!FileUtil.PARAGON_PATH.exists()) {
            logger.info("Created ${FileUtil.PARAGON_PATH} ${if (FileUtil.PARAGON_PATH.mkdir()) "successfully" else "unsuccessfully"}")
        }

        logger.info("Initialising event bus")
        bus = EventBus()
        processor = EventProcessor()

        logger.info("Initialising config manager")
        configManager = ConfigManager()

        logger.info("Initialising keyboard manager")
        keyboardManager = KeyboardManager()

        logger.info("Initialising toast manager")
        toastManager = ToastManager()

        logger.info("Initialising baritone manager")
        baritoneManager = BaritoneManager()

        logger.info("Initialising module manager")
        moduleManager = ModuleManager()

        logger.info("Initialising command manager")
        commandManager = CommandManager()

        logger.info("Initialising rotation manager")
        rotationManager = RotationManager()

        logger.info("Initialising inventory manager")
        inventoryManager = InventoryManager()

        logger.info("Initialising alt manager")
        altManager = AltManager()

        logger.info("Initialising placement manager")
        placementManager = PlacementManager()

        logger.info("Loading configurations & data")
        moduleManager.load("current")
        configManager.loadAll()

        logger.info("Running post load methods")
        moduleManager.modules.forEach(Feature::postLoad)

        logger.info("Initialising uis")
        ClickGUI.surgeUi = SurgeUI()
        ClickGUI.aestheticalUI = AestheticalUI()

        logger.info("Initialised Paragon $version")
    }

    companion object {
        @JvmStatic val version: String = BuildConfig.VERSION
        @JvmStatic var logger = LoggerFactory.getLogger("paragon")

        @JvmStatic lateinit var bus: EventBus
        @JvmStatic lateinit var processor: EventProcessor

        @JvmStatic lateinit var keyboardManager: KeyboardManager
        @JvmStatic lateinit var moduleManager: ModuleManager
        @JvmStatic lateinit var commandManager: CommandManager
        @JvmStatic lateinit var rotationManager: RotationManager
        @JvmStatic lateinit var inventoryManager: InventoryManager
        @JvmStatic lateinit var toastManager: ToastManager
        @JvmStatic lateinit var configManager: ConfigManager
        @JvmStatic lateinit var altManager: AltManager
        @JvmStatic lateinit var baritoneManager: BaritoneManager
        @JvmStatic lateinit var placementManager: PlacementManager
    }

}