package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventInputSuggestions;
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

    @Shadow
    @Final
    TextFieldWidget textField;
    @Shadow
    boolean completingSuggestions;
    @Shadow
    private @Nullable ParseResults<CommandSource> parse;
    @Shadow
    @Nullable
    private ChatInputSuggestor.@Nullable SuggestionWindow window;
    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected abstract void showCommandSuggestions();

    @Inject(method = "refresh",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void hookCommandSystem(CallbackInfo ci, String string, StringReader reader) {
        final EventInputSuggestions eventInputSuggestions = new EventInputSuggestions(reader);
        TarasandeMain.Companion.get().getManagerEvent().call(eventInputSuggestions);

        if (eventInputSuggestions.getDispatcher() != null && eventInputSuggestions.getCommandSource() != null) {
            if (this.parse == null) {
                this.parse = eventInputSuggestions.getDispatcher().parse(reader, eventInputSuggestions.getCommandSource());
            }

            final int cursor = textField.getCursor();

            if (cursor >= 1 && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = eventInputSuggestions.getDispatcher().getCompletionSuggestions(this.parse, cursor);
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
