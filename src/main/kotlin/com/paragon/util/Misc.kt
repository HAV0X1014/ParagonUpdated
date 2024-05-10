package com.paragon.util

import com.paragon.backend.setting.Colour
import kotlinx.coroutines.*
import net.minecraft.block.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.entity.EntityType
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.crash.CrashReport
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.awt.Rectangle
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author surge, bush
 * @since 19/02/2023
 */
val mc: MinecraftClient = MinecraftClient.getInstance()

// they aren't actually unattackable but we dont want to attack them by default
// i love chatgpt
val unattackables = arrayOf(
    EntityType.ARMOR_STAND,
    EntityType.ITEM_FRAME,
    EntityType.LEASH_KNOT,
    EntityType.PAINTING,
    EntityType.END_CRYSTAL,
    EntityType.EXPERIENCE_ORB,
    EntityType.FIREWORK_ROCKET,
    EntityType.GLOW_ITEM_FRAME,
    EntityType.LIGHTNING_BOLT,
    EntityType.PHANTOM,
    EntityType.PUFFERFISH,
    EntityType.SHULKER_BULLET,
    EntityType.SMALL_FIREBALL,
    EntityType.SNOWBALL,
    EntityType.SPECTRAL_ARROW,
    EntityType.TRIDENT,
    EntityType.WITHER_SKULL,
    EntityType.FALLING_BLOCK,
    EntityType.EGG,
    EntityType.ARROW,
    EntityType.BOAT,
    EntityType.MINECART,
    EntityType.ENDER_PEARL,
    EntityType.EYE_OF_ENDER,
    EntityType.FIREBALL,
    EntityType.TRADER_LLAMA,
    EntityType.WANDERING_TRADER,
    EntityType.FISHING_BOBBER,
    EntityType.DRAGON_FIREBALL,
    EntityType.LLAMA_SPIT,
    EntityType.SMALL_FIREBALL

    // might be some more idk
)

fun nullCheck() = mc.player == null || mc.world == null
fun getClientColour(): Colour = Colour(185, 19, 211, 255)

fun Color.fade(secondary: Color, factor: Double): Color {
    return Color(
        (this.red + (secondary.red - this.red) * factor.coerceIn(0.0, 1.0)).toInt(),
        (this.green + (secondary.green - this.green) * factor.coerceIn(0.0, 1.0)).toInt(),
        (this.blue + (secondary.blue - this.blue) * factor.coerceIn(0.0, 1.0)).toInt(),
        (this.alpha + (secondary.alpha - this.alpha) * factor.coerceIn(0.0, 1.0)).toInt()
    )
}

fun BlockPos.getPlaceableSide(): Direction? {
    Direction.values().forEach {
        val neighbour = this.offset(it)
        val opposite = it.opposite

        val state = mc.world!!.getBlockState(neighbour)

        if (state.isAir || state.block.interactable()) {
            return@forEach
        }

        return opposite
    }

    return null
}

fun Block.interactable(): Boolean = this is CraftingTableBlock
    || this is AnvilBlock
    || this is ButtonBlock
    || this is AbstractPressurePlateBlock
    || this is BlockWithEntity
    || this is BedBlock
    || this is FenceGateBlock
    || this is DoorBlock
    || this is NoteBlock
    || this is TrapdoorBlock

fun Number.truncate(places: Int): String {
    val split = this.toString().split('.')

    var decimals = ""

    if (split.size > 1) {
        decimals = '.' + split[1].substring(0, minOf(places + 1, split[1].lastIndex))
    }

    return split[0] + decimals
}

operator fun Vec3d.component1(): Double = x
operator fun Vec3d.component2(): Double = y
operator fun Vec3d.component3(): Double = z

fun BlockPos.box(): Box {
    return Box(this.x.toDouble(), this.y.toDouble(), this.z.toDouble(), (this.x + 1).toDouble(), (this.y + 1).toDouble(), (this.z + 1).toDouble())
}

fun InGameHud.print(content: String, id: Int = hashCode()) {
    val chatComponent = Text.literal(Formatting.LIGHT_PURPLE.toString() + "paragon" + Formatting.DARK_GRAY + " \u00BB ")
            .setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append(content)
    chatHud.addMessage(chatComponent)
}

fun String.formatCapitalised(): String {
    return buildString(this.length) {
        var isFirst = true

        this@formatCapitalised.forEach {
            if (it == '_') {
                isFirst = true
                return@forEach
            }

            if (isFirst) {
                append(' ')
                append(it.toString().uppercase())
                isFirst = false
            } else {
                append(it.toString().lowercase())
            }
        }
    }
}

fun Rectangle.offset(x: Int, y: Int): Rectangle {
    return Rectangle(
        this.x + x,
        this.y + y,
        this.width,
        this.height
    )
}

fun Rectangle.tempGrow(width: Int, height: Int): Rectangle {
    return Rectangle(
        this.x,
        this.y,
        this.width + width,
        this.height + height
    )
}

// FROM 711.CLUB vvv

private val defaultContext = Dispatchers.Default + CoroutineExceptionHandler { context, throwable ->
    mc.setCrashReportSupplier(
        CrashReport(
            """
            Paragon: An uncaught exception was thrown from a coroutine. This means something 
            bad happened that would probably make the game unplayable if it wasn't shut down.
            
            Context: $context
            
            DM the devs and tell them to fix their shitcode! (also please send them this whole log)
            
            """.trimIndent(), throwable
        )
    )
}

object Background : CoroutineScope by CoroutineScope(defaultContext), CoroutineContext by defaultContext

// BIG
fun backgroundThread(block: suspend CoroutineScope.() -> Unit) = Background.launch(block = block)

fun CoroutineScope.lazyLaunch(block: suspend CoroutineScope.() -> Unit) = launch(
    start = CoroutineStart.LAZY, block = block
)

/**
 * Runs the given block on another thread. If the delegated
 * property is called before the thread finishes, the
 * calling thread will be paused until it finishes.
 *
 * @author bush
 * @since 2/17/2022
 */
class AsyncDelegate<T>(block: suspend CoroutineScope.() -> T) : ReadWriteProperty<Any?, T> {
    private val deferred = Background.async { block() }
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        // I know this will go every time if the .await()'ed value is null, but I cba to change this
        if (value == null) value = runBlocking { deferred.await() }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> async(block: suspend CoroutineScope.() -> T) = AsyncDelegate(block)