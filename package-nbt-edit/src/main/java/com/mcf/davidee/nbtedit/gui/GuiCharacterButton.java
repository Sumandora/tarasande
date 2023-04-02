package com.mcf.davidee.nbtedit.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class GuiCharacterButton extends DrawableHelper {

	public static final int WIDTH = 14, HEIGHT = 14;

	private byte id;
	private int x, y;
	private boolean enabled;


	public GuiCharacterButton(byte id, int x, int y){
		this.id = id;
		this.x = x; 
		this.y = y;
	}
	public void draw(MatrixStack matrices, int mx, int my) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, GuiNBTNode.WIDGET_TEXTURE);

		if(inBounds(mx,my))
			fill(matrices, x, y, x+WIDTH, y+HEIGHT, 0x80ffffff);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrices, x, y, id * WIDTH, 27, WIDTH, HEIGHT);
		if (!enabled){
			fill(matrices, x, y, x+WIDTH, y+HEIGHT, 0xc0222222);
		}
	}
	
	public void setEnabled(boolean aFlag){
		enabled = aFlag;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public boolean inBounds(int mx, int my){
		return enabled && mx >= x && my >= y && mx < x + WIDTH && my < y + HEIGHT;
	}
	
	public byte getId(){
		return id;
	}
}
 