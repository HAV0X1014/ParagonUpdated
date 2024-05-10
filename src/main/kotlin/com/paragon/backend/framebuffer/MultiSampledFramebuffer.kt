package com.paragon.backend.framebuffer

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.GlStateManager.*
import com.mojang.blaze3d.systems.RenderSystem
import com.paragon.util.mc
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*

/**
 * Coffee Client's MSAA framebuffer used for reference because I have no fucking clue what I'm doing
 * @author surge
 * @since 17/02/2023
 */
class MultiSampledFramebuffer(private val samples: Int) : Framebuffer(true) {

    private var rboColour = 0
    private var rboDepth = 0
    private var using = false

    init {
        setClearColor(1f, 1f, 1f, 0f)
    }

    override fun resize(width: Int, height: Int, getError: Boolean) {
        if (textureWidth != width || textureHeight != height) {
            super.resize(width, height, getError)
        }
    }

    override fun initFbo(width: Int, height: Int, getError: Boolean) {
        RenderSystem.assertOnRenderThreadOrInit()
        val maxSize = RenderSystem.maxSupportedTextureSize()

        require(!(width <= 0 || width > maxSize || height <= 0 || height > maxSize)) { "Window " + width + "x" + height + " size out of bounds (max. size: " + maxSize + ")" }

        viewportWidth = width
        viewportHeight = height
        textureWidth = width
        textureHeight = height

        fbo = GL30.glGenFramebuffers()

        _glBindFramebuffer(GL_FRAMEBUFFER, fbo)

        rboColour = GL30.glGenRenderbuffers()

        _glBindRenderbuffer(GL_RENDERBUFFER, rboColour)
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL11.GL_RGBA8, width, height)
        _glBindRenderbuffer(GL_RENDERBUFFER, 0)

        rboDepth = GL30.glGenRenderbuffers()

        _glBindRenderbuffer(GL_RENDERBUFFER, rboDepth)
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL11.GL_DEPTH_COMPONENT, width, height)
        _glBindRenderbuffer(GL_RENDERBUFFER, 0)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rboColour)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth)

        colorAttachment = mc.framebuffer.colorAttachment
        depthAttachment = mc.framebuffer.depthAttachment

        checkFramebufferStatus()
        this.clear(getError)
        endRead()
    }

    override fun delete() {
        RenderSystem.assertOnRenderThreadOrInit()
        endRead()
        endWrite()

        if (fbo > -1) {
            _glBindFramebuffer(GL_FRAMEBUFFER, 0)
            GlStateManager._glDeleteFramebuffers(fbo)
            fbo = -1
        }

        if (rboColour > -1) {
            GlStateManager._glDeleteRenderbuffers(rboColour)

            rboColour = -1
        }
        if (rboDepth > -1) {
            GlStateManager._glDeleteRenderbuffers(rboDepth)
            rboDepth = -1
        }

        colorAttachment = -1
        depthAttachment = -1
        textureWidth = -1
        textureHeight = -1
    }

    override fun beginWrite(setViewport: Boolean) {
        super.beginWrite(setViewport)

        if (!using) {
            ACTIVE.add(this)
            using = true
        }
    }

    override fun endWrite() {
        super.endWrite()

        if (using) {
            ACTIVE.remove(this)
            using = false
        }
    }

    companion object {
        private const val MINIMUM = 2
        private val INSTANCES: MutableMap<Int, MultiSampledFramebuffer> = HashMap()
        private val ACTIVE: MutableList<MultiSampledFramebuffer> = ArrayList()

        fun getInstance(samples: Int): MultiSampledFramebuffer {
            return INSTANCES.computeIfAbsent(samples) { x: Int? -> MultiSampledFramebuffer(samples) }
        }

        @JvmStatic
        fun use(block: Runnable) {
            use(32.coerceAtMost(MINIMUM), mc.framebuffer, block)
        }

        private fun use(samples: Int, main: Framebuffer, block: Runnable) {
            RenderSystem.assertOnRenderThread()

            val framebuffer = getInstance(samples)
            framebuffer.resize(main.textureWidth, main.textureHeight, true)

            _glBindFramebuffer(GL_READ_FRAMEBUFFER, main.fbo)
            _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer.fbo)
            _glBlitFrameBuffer(0, 0, framebuffer.textureWidth, framebuffer.textureHeight, 0, 0, framebuffer.textureWidth, framebuffer.textureHeight, GL_COLOR_BUFFER_BIT, GL_LINEAR)

            framebuffer.beginWrite(true)
            block.run()
            framebuffer.endWrite()

            _glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer.fbo)
            _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, main.fbo)
            _glBlitFrameBuffer(0, 0, framebuffer.textureWidth, framebuffer.textureHeight, 0, 0, framebuffer.textureWidth, framebuffer.textureHeight, GL_COLOR_BUFFER_BIT, GL_LINEAR)

            framebuffer.clear(true)
            main.beginWrite(false)
        }
    }

}