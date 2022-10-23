package net.tarasandedevelopment.tarasande.mixin.mixins.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggestor {

    @Shadow @Final
    MinecraftClient client;

    @Shadow private @Nullable ParseResults<CommandSource> parse;

    @Shadow @Final
    TextFieldWidget textField;

    @Shadow @Nullable private ChatInputSuggestor.@Nullable SuggestionWindow window;

    @Shadow private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow protected abstract void showCommandSuggestions();

    @Shadow
    boolean completingSuggestions;

    @Inject(method = "refresh",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void hookCommandSystem(CallbackInfo ci, String string, StringReader reader) {
        final String prefix = TarasandeMain.Companion.get().getClientValues().getCommandsPrefix().getValue();
        final int length = prefix.length();

        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor())) {
            reader.setCursor(reader.getCursor() + length);
            assert this.client.player != null;
            final CommandDispatcher<CommandSource> commandDispatcher = TarasandeMain.Companion.get().getManagerCommand().getDispatcher();

            if (this.parse == null) {
                this.parse = commandDispatcher.parse(reader, TarasandeMain.Companion.get().getManagerCommand().getCommandSource());
            }

            final int cursor = textField.getCursor();

            if (cursor >= 1 && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.showCommandSuggestions();
                    }
                });
            }
            ci.cancel();
        }
    }
}
