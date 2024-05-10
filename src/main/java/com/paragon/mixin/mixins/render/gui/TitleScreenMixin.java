package com.paragon.mixin.mixins.render.gui;

import com.paragon.client.ui.title.MainMenuHook;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.paragon.util.MiscKt.getMc;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At(value = "TAIL"))
	public void hookInit(CallbackInfo ci) {
		MainMenuHook.INSTANCE.init();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER))
	public void hookRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		MainMenuHook.INSTANCE.setHeight(height);
		MainMenuHook.INSTANCE.render(matrices, (int) getMc().mouse.getX(), (int) getMc().mouse.getY(), delta);
	}

	@Inject(method = "mouseClicked", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	public void hookMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (MainMenuHook.INSTANCE.mouseClicked((int) getMc().mouse.getX(), (int) getMc().mouse.getY(), button)) {
			cir.setReturnValue(true);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (MainMenuHook.INSTANCE.mouseScrolled((int) getMc().mouse.getX(), (int) getMc().mouse.getY(), (float) amount)) {
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (MainMenuHook.INSTANCE.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		MainMenuHook.INSTANCE.charTyped(chr, modifiers);
		
		return super.charTyped(chr, modifiers);
	}

}
