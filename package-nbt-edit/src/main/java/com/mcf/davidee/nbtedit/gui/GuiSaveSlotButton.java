/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/16/22, 12:37 AM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.nbt.SaveStates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class GuiSaveSlotButton extends DrawableHelper {

	public static final Identifier TEXTURE = new Identifier("textures/gui/widgets.png");
	private static final int X_SIZE = 14, HEIGHT = 20, MAX_WIDTH = 150, MIN_WIDTH = 82, GAP = 3;
	public final SaveStates.SaveState save;
	private final MinecraftClient mc;
	private final int rightX;

	private int x;
	private final int y;
	private int width;
	private String text;
	private boolean xVisible;

	private int tickCount;
	private final ButtonWidget vanillaWidget;

	public GuiSaveSlotButton(SaveStates.SaveState save, int rightX, int y) {
		this.save = save;
		this.rightX = rightX;
		this.y = y;
		mc = MinecraftClient.getInstance();
		xVisible = save.tag != null && !save.tag.asString().equals("{}");
		text = (!xVisible ? "Save " : "Load ") + save.name;
		tickCount = -1;
		updatePosition();

		this.vanillaWidget = ButtonWidget.builder(Text.literal(""), (button -> {
		})).dimensions(x, y, width, 20).build();
	}


	public void draw(MatrixStack matrices, int mx, int my) {
		int textColor = ((inBounds(mx, my))) ? 16777120 : 0xffffff;
		renderVanillaButton(matrices, mx, my, x, y, 0, 66, width, HEIGHT);
		drawCenteredTextWithShadow(matrices, mc.textRenderer, text, x + width / 2, y + 6, textColor);
		if (tickCount != -1 && tickCount / 6 % 2 == 0) {
			mc.textRenderer.drawWithShadow(matrices, "_", x + (width + mc.textRenderer.getWidth(text)) / 2F + 1, y + 6, 0xffffff);
		}

		if (xVisible) {
			textColor = ((inBoundsOfX(mx, my))) ? 16777120 : 0xffffff;
			renderVanillaButton(matrices, mx, my, leftBoundOfX(), topBoundOfX(), 0, 66, X_SIZE, X_SIZE);
			drawCenteredTextWithShadow(matrices, mc.textRenderer, "x", x - GAP - X_SIZE / 2, y + 6, textColor);
		}
	}

	private void renderVanillaButton(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int u, int v, int width, int height) {
		this.vanillaWidget.setX(x);
		this.vanillaWidget.setY(y);
		this.vanillaWidget.setWidth(width);

		this.vanillaWidget.render(matrices, mouseX, mouseY, 0);
	}

	private int leftBoundOfX() {
		return x - X_SIZE - GAP;
	}

	private int topBoundOfX() {
		return y + (HEIGHT - X_SIZE) / 2;
	}

	public boolean inBoundsOfX(int mx, int my) {
		int buttonX = leftBoundOfX();
		int buttonY = topBoundOfX();
		return xVisible && mx >= buttonX && my >= buttonY && mx < buttonX + X_SIZE && my < buttonY + X_SIZE;
	}

	public boolean inBounds(int mx, int my) {
		return mx >= x && my >= y && mx < x + width && my < y + HEIGHT;
	}

	private void updatePosition() {
		width = mc.textRenderer.getWidth(text) + 24;
		if (width % 2 == 1) ++width;
		width = MathHelper.clamp(width, MIN_WIDTH, MAX_WIDTH);
		x = rightX - width;
	}

	public void reset() {
		xVisible = false;
		save.tag = new NbtCompound();
		text = "Save " + save.name;
		updatePosition();
	}

	public void saved() {
		xVisible = true;
		text = "Load " + save.name;
		updatePosition();
	}

	public void keyTyped(char c, int key) {
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			backSpace();
		}
		if (Character.isDigit(c) || Character.isLetter(c)) {
			save.name += c;
			text = (save.tag == null ? "Save " : "Load ") + save.name;
			updatePosition();
		}
	}


	public void backSpace() {
		if (save.name.length() > 0) {
			save.name = save.name.substring(0, save.name.length() - 1);
			text = (save.tag == null ? "Save " : "Load ") + save.name;
			updatePosition();
		}
	}

	public void startEditing() {
		tickCount = 0;
	}

	public void stopEditing() {
		tickCount = -1;
	}


	public void update() {
		++tickCount;
	}

}
