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

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import com.mcf.davidee.nbtedit.nbt.ParseHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GuiEditNBT extends DrawableHelper {

	public static final Identifier WINDOW_TEXTURE = new Identifier("nbtedit", "textures/gui/window.png");

	public static final int WIDTH = 178, HEIGHT = 93;

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final Node<NamedNBT> node;
	private final NbtElement nbt;
	private final boolean canEditText;
	private final boolean canEditValue;
	private final GuiNBTTree parent;

	private int x, y;

	private TextFieldWidget key,  value;
	private ButtonWidget save, cancel;
	private String kError, vError;

	private GuiCharacterButton newLine, section;


	public GuiEditNBT(GuiNBTTree parent, Node<NamedNBT> node, boolean editText, boolean editValue){
		this.parent = parent;
		this.node = node;
		this.nbt = node.getObject().getNBT();
		canEditText = editText;
		canEditValue = editValue;
	}

	public void initGUI(int x, int y){
		this.x=x;
		this.y=y;

		section = new GuiCharacterButton((byte)0,x+WIDTH-1,y+34);
		newLine = new GuiCharacterButton((byte)1,x+WIDTH-1,y+50);
		String sKey = (key == null) ? node.getObject().getName() : key.getText();
		String sValue = (value == null) ? getValue(nbt) : value.getText();
		this.key = new TextFieldWidget(mc.textRenderer,x+46,y+18,116,15, Text.of(""));
		this.value = new TextFieldWidget(mc.textRenderer,x+46,y+44,116,15, Text.of(""));

		key.setText(sKey);
		key.setDrawsBackground(false);
		key.setEditable(canEditText);
		value.setMaxLength(256);
		value.setText(sValue);
		value.setDrawsBackground(false);
		value.setEditable(canEditValue);
		save = ButtonWidget.builder(Text.literal("Save"), (b) -> {
			saveAndQuit();
		}).dimensions(x + 9,y + 62,75,20).build();
		if(!key.isFocused() && !value.isFocused()){
			if (canEditText)
				key.setFocused(true);
			else if (canEditValue)
				value.setFocused(true);
		}
		section.setEnabled(value.isFocused());
		newLine.setEnabled(value.isFocused());
		cancel = ButtonWidget.builder(Text.literal("Cancel"), button -> {
			parent.nodeEdited(node);
			parent.closeWindow();
		}).dimensions(x + 93,y + 62,75,20).build();
	}

	public void click(int mx, int my, int i){
		if (newLine.inBounds(mx, my) && value.isFocused()){
			value.write("\n");
			checkValidInput();
		}
		else if (section.inBounds(mx,my) && value.isFocused()){
			value.write("" + NBTStringHelper.SECTION_SIGN);
			checkValidInput();
		}
		else{
			key.mouseClicked(mx, my, 0);
			value.mouseClicked(mx, my, 0);

			save.mouseClicked(mx, my, i);
			cancel.mouseClicked(mx, my, i);

			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
		}
	}

	private void saveAndQuit(){
		try {
			if (canEditText)
				node.getObject().setName(key.getText());
			setValidValue(node, value.getText());
			parent.nodeEdited(node);
			parent.closeWindow();
		} catch (Exception ignored) {}
	}

	public void draw(MatrixStack matrices, int mx, int my, float delta) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, WINDOW_TEXTURE);

		GL11.glColor4f(1, 1, 1, 1);
		drawTexture(matrices, x,y,0,0,WIDTH,HEIGHT);
		if (!canEditText)
			fill(matrices, x+42, y+15, x+169, y+31, 0x80000000);
		if(!canEditValue)
			fill(matrices, x+42, y+41, x+169, y+57, 0x80000000);
		key.render(matrices, mx, my, delta);
		value.render(matrices, mx, my, delta);

		save.render(matrices, mx, my, delta);
		cancel.render(matrices, mx, my, delta);

		if (kError != null)
			drawCenteredString(matrices, mc.textRenderer, kError, x+WIDTH/2, y+4, 0xFF0000);
		if (vError != null)
			drawCenteredString(matrices, mc.textRenderer,vError,x+WIDTH/2,y+32,0xFF0000);

		newLine.draw(matrices, mx, my);
		section.draw(matrices, mx, my);
	}

	public void drawCenteredString(MatrixStack matrices, TextRenderer text, String par2Str, int par3, int par4, int par5) {
		text.draw(matrices, par2Str, par3 - text.getWidth(par2Str) / 2F, par4, par5);
	}

	public void update() {
		value.tick();
		key.tick();
	}

	public void charTyped(char chr, int keyCode) {
		if (key.isFocused())
			key.charTyped(chr, keyCode);

		if (value.isFocused())
			value.charTyped(chr, keyCode);
	}

	public void keyTyped(char c, int i, int b) {
		if (i == GLFW.GLFW_KEY_ESCAPE){
			parent.closeWindow();
		}
		else if (i == GLFW.GLFW_KEY_TAB){
			if (key.isFocused() && canEditValue){
				key.setFocused(false);
				value.setFocused(true);
			}
			else if (value.isFocused() && canEditText){
				key.setFocused(true);
				value.setFocused(false);
			}
			section.setEnabled(value.isFocused());
			newLine.setEnabled(value.isFocused());
		}
		else if (i == GLFW.GLFW_KEY_BACKSPACE) {
			checkValidInput();
			if (save.active)
				saveAndQuit();
		}
		else{
			key.keyPressed(c, i, b);
			value.keyPressed(c, i, b);
			checkValidInput();
		}
	}



	private void checkValidInput(){
		boolean valid = true;
		kError = null;
		vError = null;
		if (canEditText && !validName()){
			valid = false;
			kError = "Duplicate Tag Name";
		}
		try {
			validValue(value.getText(),nbt.getType());
			valid &= true;
		}
		catch(NumberFormatException e){
			vError = e.getMessage();
			valid = false;
		}
		save.active = valid;
	}

	private boolean validName(){
		for (Node<NamedNBT> node : this.node.getParent().getChildren()){
			NbtElement base = node.getObject().getNBT();
			if (base != nbt && node.getObject().getName().equals(key.getText()))
				return false;
		}
		return true;
	}

	private static void setValidValue(Node<NamedNBT> node, String value){
		NamedNBT named = node.getObject();
		NbtElement base = named.getNBT();

		if (base instanceof NbtByte)
			named.setNBT(NbtByte.of(ParseHelper.parseByte(value)));
		if (base instanceof NbtShort)
			named.setNBT(NbtShort.of(ParseHelper.parseShort(value)));
		if (base instanceof NbtInt)
			named.setNBT(NbtInt.of(ParseHelper.parseInt(value)));
		if (base instanceof NbtLong)
			named.setNBT(NbtLong.of(ParseHelper.parseLong(value)));
		if(base instanceof NbtFloat)
			named.setNBT(NbtFloat.of(ParseHelper.parseFloat(value)));
		if(base instanceof NbtDouble)
			named.setNBT(NbtDouble.of(ParseHelper.parseDouble(value)));
		if(base instanceof NbtByteArray)
			named.setNBT(new NbtByteArray(ParseHelper.parseByteArray(value)));
		if(base instanceof NbtIntArray)
			named.setNBT(new NbtIntArray(ParseHelper.parseIntArray(value)));
		if (base instanceof NbtString)
			named.setNBT(NbtString.of(value));
	}

	private static void validValue(String value, byte type) throws NumberFormatException{
		switch (type) {
			case 1 -> ParseHelper.parseByte(value);
			case 2 -> ParseHelper.parseShort(value);
			case 3 -> ParseHelper.parseInt(value);
			case 4 -> ParseHelper.parseLong(value);
			case 5 -> ParseHelper.parseFloat(value);
			case 6 -> ParseHelper.parseDouble(value);
			case 7 -> ParseHelper.parseByteArray(value);
			case 11 -> ParseHelper.parseIntArray(value);
		}
	}

	private static String getValue(NbtElement base){
		switch (base.getType()) {
			case 7 -> {
				String s = "";
				for (byte b : ((NbtByteArray) base).getByteArray() /*byteArray*/) {
					s += b + " ";
				}
				return s;
			}
			case 9 -> {
				return "TagList";
			}
			case 10 -> {
				return "TagCompound";
			}
			case 11 -> {
				String i = "";
				for (int a : ((NbtIntArray) base).getIntArray() /*intArray*/) {
					i += a + " ";
				}
				return i;
			}
			default -> {
				return NBTStringHelper.toString(base);
			}
		}
	}
}
