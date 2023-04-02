package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class GuiTextField extends DrawableHelper {

    private final TextRenderer fontRenderer;

    private final int xPos, yPos;
    private final int width, height;


    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;

    private boolean isFocused = false;


    private boolean isEnabled = true;
    private int field_73816_n = 0;
    private int cursorPosition = 0;

    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd = 0;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;

    /**
     * True if this textbox is visible
     */
    private boolean visible = true;
    private boolean enableBackgroundDrawing = true;
    private final boolean allowSection;

    public GuiTextField(TextRenderer par1FontRenderer, int x, int y, int w, int h, boolean allowSection) {
        this.fontRenderer = par1FontRenderer;
        this.xPos = x;
        this.yPos = y;
        this.width = w;
        this.height = h;
        this.allowSection = allowSection;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    /**
     * Returns the text beign edited on the textbox.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text of the textbox.
     */
    public void setText(String par1Str) {
        if (par1Str.length() > this.maxStringLength) {
            this.text = par1Str.substring(0, this.maxStringLength);
        } else {
            this.text = par1Str;
        }

        this.setCursorPositionEnd();
    }

    /**
     * @return returns the text between the cursor and selectionEnd
     */
    public String getSelectedtext() {
        int var1 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(var1, var2);
    }

    public boolean charTyped(char chr, int keyCode) {
        if (!this.isEnabled) {
            return false;
        } else if (SharedConstants.isValidChar(chr)) {
            this.writeText(Character.toString(chr));

            return true;
        } else {
            return false;
        }
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(String par1Str) {
        String var2 = "";
        String var3 = CharacterFilter.filerAllowedCharacters(par1Str, allowSection);
        int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int var6 = this.maxStringLength - this.text.length() - (var4 - this.selectionEnd);

        if (this.text.length() > 0) {
            var2 = var2 + this.text.substring(0, var4);
        }

        int var8;

        if (var6 < var3.length()) {
            var2 = var2 + var3.substring(0, var6);
            var8 = var6;
        } else {
            var2 = var2 + var3;
            var8 = var3.length();
        }

        if (this.text.length() > 0 && var5 < this.text.length()) {
            var2 = var2 + this.text.substring(var5);
        }

        this.text = var2;
        this.moveCursorBy(var4 - this.selectionEnd + var8);
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean var2 = par1 < 0;
                int var3 = var2 ? this.cursorPosition + par1 : this.cursorPosition;
                int var4 = var2 ? this.cursorPosition : this.cursorPosition + par1;
                String var5 = "";

                if (var3 >= 0) {
                    var5 = this.text.substring(0, var3);
                }

                if (var4 < this.text.length()) {
                    var5 = var5 + this.text.substring(var4);
                }

                this.text = var5;

                if (var2) {
                    this.moveCursorBy(par1);
                }
            }
        }
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int par1) {
        return this.getNthWordFromPos(par1, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int par1, int par2) {
        return this.func_73798_a(par1, this.getCursorPosition(), true);
    }

    public int func_73798_a(int par1, int par2, boolean par3) {
        int var4 = par2;
        boolean var5 = par1 < 0;
        int var6 = Math.abs(par1);

        for (int var7 = 0; var7 < var6; ++var7) {
            if (var5) {
                while (par3 && var4 > 0 && this.text.charAt(var4 - 1) == 32) {
                    --var4;
                }

                while (var4 > 0 && this.text.charAt(var4 - 1) != 32) {
                    --var4;
                }
            } else {
                int var8 = this.text.length();
                var4 = this.text.indexOf(32, var4);

                if (var4 == -1) {
                    var4 = var8;
                } else {
                    while (par3 && var4 < var8 && this.text.charAt(var4) == 32) {
                        ++var4;
                    }
                }
            }
        }

        return var4;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int par1) {
        this.setCursorPosition(this.selectionEnd + par1);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from you GuiScreen to process the keys into textbox.
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isEnabled) {
            return false;
        } else {
            if (Screen.isSelectAll(keyCode)) {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            } else if (Screen.isCopy(keyCode)) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedtext());
                return true;
            } else if (Screen.isPaste(keyCode)) {
                this.writeText(MinecraftClient.getInstance().keyboard.getClipboard());

                return true;
            } else if (Screen.isCut(keyCode)) {
//                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedtext());
//                writeText("");

                return true;
            } else {
                switch (keyCode) {
                    case 259:
						this.deleteWords(-1);
                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                    case 261:
                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.setCursorPosition(this.getCursorPosition());
                        } else {
                            this.moveCursorBy(1);
                        }

                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.setCursorPosition(this.getCursorPosition());
                        } else {
                            this.moveCursorBy(-1);
                        }

                        return true;
                    case 268:
                        return true;
                    case 269:
                        return true;
                }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int par1, int par2, int par3) {
        String displayString = text.replace(NBTStringHelper.SECTION_SIGN, '?');
        boolean var4 = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;

        this.setFocused(this.isEnabled && var4);

        if (this.isFocused && par3 == 0) {
            int var5 = par1 - this.xPos;

            if (this.enableBackgroundDrawing) {
                var5 -= 4;
            }

            String var6 = this.fontRenderer.trimToWidth(displayString.substring(this.field_73816_n), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimToWidth(var6, var5).length() + this.field_73816_n);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox(MatrixStack matrices) {
        String textToDisplay = text.replace(NBTStringHelper.SECTION_SIGN, '?');
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                fill(matrices, this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
                fill(matrices, this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
            }

            int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int var2 = this.cursorPosition - this.field_73816_n;
            int var3 = this.selectionEnd - this.field_73816_n;
            String var4 = this.fontRenderer.trimToWidth(textToDisplay.substring(this.field_73816_n), this.getWidth());
            boolean var5 = var2 >= 0 && var2 <= var4.length();
            boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
            int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
            int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
            int var9 = var7;

            if (var3 > var4.length()) {
                var3 = var4.length();
            }

            if (var4.length() > 0) {
                String var10 = var5 ? var4.substring(0, var2) : var4;
                var9 = this.fontRenderer.drawWithShadow(matrices, var10, var7, var8, var1);
            }

            boolean var13 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int var11 = var9;

            if (!var5) {
                var11 = var2 > 0 ? var7 + this.width : var7;
            } else if (var13) {
                var11 = var9 - 1;
                --var9;
            }

            if (var4.length() > 0 && var5 && var2 < var4.length()) {
                this.fontRenderer.drawWithShadow(matrices, var4.substring(var2), var9, var8, var1);
            }

            if (var6) {
                if (var13) {
                    fill(matrices, var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRenderer.fontHeight, -3092272);
                } else {
                    this.fontRenderer.drawWithShadow(matrices, "_", var11, var8, var1);
                }
            }

            if (var3 != var2) {
                int var12 = var7 + this.fontRenderer.getWidth(var4.substring(0, var3));
                this.drawCursorVertical(matrices, var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.fontHeight);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(MatrixStack matrices, int par1, int par2, int par3, int par4) {
        int var5;

        if (par1 < par3) {
            var5 = par1;
            par1 = par3;
            par3 = var5;
        }

        if (par2 < par4) {
            var5 = par2;
            par2 = par4;
            par4 = var5;
        }
        fill(matrices, par1, par2, par3, par4, Integer.MIN_VALUE);
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public void setMaxStringLength(int par1) {
        this.maxStringLength = par1;

        if (this.text.length() > par1) {
            this.text = this.text.substring(0, par1);
        }
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return this.cursorPosition;
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int par1) {
        this.cursorPosition = par1;
        int var2 = this.text.length();

        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > var2) {
            this.cursorPosition = var2;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * get enable drawing background and outline
     */
    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    /**
     * enable drawing background and outline
     */
    public void setEnableBackgroundDrawing(boolean par1) {
        this.enableBackgroundDrawing = par1;
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(int par1) {
        this.enabledColor = par1;
    }

    public void func_82266_h(int par1) {
        this.disabledColor = par1;
    }

    /**
     * getter for the focused field
     */
    public boolean isFocused() {
        return this.isFocused;
    }

    /**
     * setter for the focused field
     */
    public void setFocused(boolean par1) {
        if (par1 && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = par1;
    }

    public void func_82265_c(boolean par1) {
        this.isEnabled = par1;
    }

    /**
     * the side of the selection that is not the cursor, maye be the same as the cursor
     */
    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if the the box is enabled
     */
    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int par1) {
        String displayString = text.replace(NBTStringHelper.SECTION_SIGN, '?');
        int var2 = displayString.length();

        if (par1 > var2) {
            par1 = var2;
        }

        if (par1 < 0) {
            par1 = 0;
        }

        this.selectionEnd = par1;

        if (this.fontRenderer != null) {
            if (this.field_73816_n > var2) {
                this.field_73816_n = var2;
            }

            int var3 = this.getWidth();
            String var4 = this.fontRenderer.trimToWidth(displayString.substring(this.field_73816_n), var3);
            int var5 = var4.length() + this.field_73816_n;

            if (par1 == this.field_73816_n) {
                this.field_73816_n -= this.fontRenderer.trimToWidth(displayString, var3, true).length();
            }

            if (par1 > var5) {
                this.field_73816_n += par1 - var5;
            } else if (par1 <= this.field_73816_n) {
                this.field_73816_n -= this.field_73816_n - par1;
            }

            if (this.field_73816_n < 0) {
                this.field_73816_n = 0;
            }

            if (this.field_73816_n > var2) {
                this.field_73816_n = var2;
            }
        }
    }


    /**
     * @return {@code true} if this textbox is visible
     */
    public boolean getVisible() {
        return this.visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(boolean par1) {
        this.visible = par1;
    }
}
