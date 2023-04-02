package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.nbt.NBTTree;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GuiEditNBTTree extends Screen {

	public final int entityOrX, y, z;
	private boolean entity;
	protected String screenTitle;
	private GuiNBTTree guiTree;

	public GuiEditNBTTree(int entity, NbtCompound tag){
		super(Text.literal("EditNBTTree"));
		this.entity =true;
		entityOrX = entity;
		y =0;
		z =0;
		screenTitle =  "NBTEdit -- EntityId #" + entityOrX;
		guiTree = new GuiNBTTree(new NBTTree(tag));
	}
	public GuiEditNBTTree(int x, int y, int z, NbtCompound tag){
		super(Text.literal("EditNBTTree"));
		this.entity = false;
		entityOrX = x;
		this.y =y;
		this.z =z;
		screenTitle =  "NBTEdit -- TileEntity at ("+x+","+y+","+z+")";
		guiTree = new GuiNBTTree(new NBTTree(tag));
	}

	@Override
	public void init() {
		guiTree.initGUI(width, height, height - 35);

		addDrawableChild(ButtonWidget.builder(Text.literal("Save"), (b) -> quitWithSave()).dimensions(width / 4 - 100, this.height -27, 200, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Quit"), (b) -> quitWithoutSaving()).dimensions(width * 3 / 4 -100, this.height -27, 200, 20).build());
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		GuiEditNBT window = guiTree.getWindow();
		if (window != null)
			window.charTyped(chr, keyCode);
		else{
			guiTree.keyTyped(chr, keyCode);
		}
		return super.charTyped(chr, keyCode);
	}

	@Override
	public boolean keyPressed(int par1, int key, int modifiers) {
		GuiEditNBT window = guiTree.getWindow();
		if (window != null)
			window.keyTyped((char) par1, key, modifiers);
		else{
			if (key == 1){
				if (guiTree.isEditingSlot())
					guiTree.stopEditingSlot();
				else
					quitWithoutSaving();
			}
			else if (key == GLFW.GLFW_KEY_DELETE)
				guiTree.deleteSelected();
			else if (key == GLFW.GLFW_KEY_BACKSPACE)
				guiTree.editSelected();
			else if (key == GLFW.GLFW_KEY_UP)
				guiTree.arrowKeyPressed(true);
			else if (key == GLFW.GLFW_KEY_DOWN)
				guiTree.arrowKeyPressed(false);
			else
				guiTree.keyTyped((char) par1, key);
		}
		return super.keyPressed(par1, key, modifiers);
	}

	@Override
	public boolean mouseClicked(double x, double y, int t) {
		if (guiTree.getWindow() == null)
			super.mouseClicked(x, y, t);
		if (t == 0)
			guiTree.mouseClicked((int)x, (int)y, t);
		if (t == 1)
			guiTree.rightClick((int)x, (int)y);

		return super.mouseClicked(x, y, t);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int ofs = (int) amount;

		if (ofs != 0){
			guiTree.shift((ofs >= 1) ? 6 : -6);
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void tick() {
		if (!client.player.isAlive())
			quitWithoutSaving();
		else
			guiTree.updateScreen();
	}

	private void quitWithSave() {
		try {
			int slot = client.player.getInventory().getSlotWithStack(client.player.getMainHandStack());
			client.player.getInventory().getStack(slot).setNbt(guiTree.getNBTTree().toNBTTagCompound());

			client.player.sendAbilitiesUpdate();
		} catch (Exception ignored) {}

		quitWithoutSaving();
	}
	
	private void quitWithoutSaving() {
		client.setScreen(null);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float par3) {
		this.renderBackground(matrices);
		guiTree.draw(matrices, x, y, par3);
		drawCenteredTextWithShadow(matrices, client.textRenderer, this.screenTitle, this.width / 2, 5, 16777215);
		if (guiTree.getWindow() == null)
			super.render(matrices, x, y, par3);
		else
			super.render(matrices,-1, -1, par3);
	}

	public boolean doesGuiPauseGame() {
		return true;
	}
	
	public Entity getEntity() {
		return entity ?  client.world.getEntityById(entityOrX) : null;
	}
	
	public boolean isTileEntity() {
		return !entity;
	}

	public int getBlockX() {
		return entity ? 0 : entityOrX;
	}

}
