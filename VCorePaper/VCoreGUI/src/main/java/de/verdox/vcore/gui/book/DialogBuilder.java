/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.gui.book;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 18:25
 */
public class DialogBuilder {
    private final VCorePaperPlugin plugin;
    private final Player player;
    private final BookGUI bookGUI;
    private final List<DialogLine> dialogLines = new ArrayList<>();
    private final Map<String, Integer> sections = new LinkedHashMap<>();
    private int lineCounter = 0;
    private int pageCounter = 1;
    private boolean isBlankLine = true;

    public DialogBuilder(@NotNull VCorePaperPlugin plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        bookGUI = new BookGUI(plugin, player);
    }

    public void openDialog() {
        getBookGUI().openBook();
    }

    private BookGUI createBookGUI() {
        bookGUI.provideBook(() -> {
            Book.Builder builder = Book.builder();
            int lineCounter = 0;
            TextComponent textComponent = Component.text("");
            for (DialogLine line : dialogLines) {

                if (lineCounter > 0)
                    textComponent = textComponent.append(Component.newline());
                if (line instanceof DialogButton)
                    textComponent = textComponent.append(bookGUI.createResponsiveCallbackText(line.getLine(), ((DialogButton) line).getConsumer()));
                else if (line instanceof DialogSectionJump) {
                    String identifier = ((DialogSectionJump) line).getSectionIdentifier();
                    if (sections.containsKey(identifier)) {
                        int pageToJumpTo = sections.get(identifier);
                        textComponent = textComponent.append(line.getLine().clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, pageToJumpTo + "")));
                    }

                } else if (!(line instanceof DialogPageBreak) && (!(line instanceof DialogSection)))
                    textComponent = textComponent.append(line.getLine());

                // Every 14 Lines -> New Page
                if (lineCounter >= 11 || line instanceof DialogPageBreak) {
                    builder.addPage(textComponent);
                    textComponent = Component.text("");
                    lineCounter = 0;
                } else if (!(line instanceof DialogSection))
                    lineCounter++;
            }
            builder.addPage(textComponent);
            return builder.build();
        });
        return bookGUI;
    }

    public DialogBuilder addText(@NotNull String text) {
        return addText(text, null);
    }

    public DialogBuilder addText(@NotNull String text, @Nullable HoverEvent<?> hoverEvent) {
        List<String> lines = VCoreUtil.BukkitUtil.getBukkitBookUtil().getLines(text);
        lines.forEach(line -> {
            TextComponent component = Component.text(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', line))).color(TextColor.fromHexString("#774936"));
            if (hoverEvent != null)
                component = component.hoverEvent(hoverEvent);
            dialogLines.add(new DialogLine(component));
            isBlankLine = true;
            countLine();
        });
        return this;
    }

    public DialogBuilder newLine() {
        dialogLines.add(new DialogLine(Component.newline()));
        if (isBlankLine)
            countLine();
        isBlankLine = false;
        return this;
    }

    public DialogBuilder addButton(@NotNull String text, @NotNull Consumer<Player> consumer) {
        return addButton(text, "Klicke mich", consumer);
    }

    public DialogBuilder addButton(@NotNull String text, @NotNull String hoverText, @NotNull Consumer<Player> consumer) {
        List<String> lines = VCoreUtil.BukkitUtil.getBukkitBookUtil().getLines(text);
        lines.forEach(line -> {
            TextComponent component = Component.text(line).hoverEvent(HoverEvent.showText(Component.text(hoverText).color(TextColor.fromHexString("#ffbfa0"))));
            dialogLines.add(new DialogButton(component, consumer));
            isBlankLine = true;
            countLine();
        });
        return this;
    }

    public DialogBuilder addPageJump(@NotNull String text, int page) {
        return addPageJump(text, "Klicke mich", page);
    }

    public DialogBuilder addPageJump(@NotNull String text, @NotNull String hoverText, int page) {
        List<String> lines = VCoreUtil.BukkitUtil.getBukkitBookUtil().getLines(text);
        lines.forEach(line -> {
            TextComponent component = Component.text(line).hoverEvent(HoverEvent.showText(Component.text(hoverText).color(TextColor.fromHexString("#ffbfa0")))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, page + ""));
            dialogLines.add(new DialogPageJump(component, page));
            isBlankLine = true;
            countLine();
        });
        return this;
    }

    public DialogBuilder addSection(@NotNull String identifier) {
        dialogLines.add(new DialogSection(Component.text(identifier)));
        sections.put(identifier, pageCounter);
        return this;
    }

    public DialogBuilder addSectionJump(@NotNull String identifier, @NotNull String text) {
        return addSectionJump(identifier, "Klicke mich", text);
    }

    public DialogBuilder addSectionJump(@NotNull String identifier, @NotNull String hoverText, @NotNull String text) {
        dialogLines.add(new DialogSectionJump(identifier, Component.text(text).hoverEvent(HoverEvent.showText(Component.text(hoverText).color(TextColor.fromHexString("#ffbfa0"))))));
        isBlankLine = true;
        countLine();
        return this;
    }

    public DialogBuilder newPage() {
        dialogLines.add(new DialogPageBreak());
        pageCounter++;
        lineCounter = 0;
        isBlankLine = true;
        return this;
    }

    private void countLine() {
        if (lineCounter >= 11) {
            lineCounter = 0;
            pageCounter++;
        } else
            lineCounter++;
    }

    public BookGUI getBookGUI() {
        return createBookGUI();
    }

    public Player getPlayer() {
        return player;
    }

    public List<DialogLine> getDialogLines() {
        return dialogLines;
    }

    public static class DialogLine {
        private final TextComponent line;

        public DialogLine(@NotNull TextComponent line) {
            this.line = line;
        }

        public TextComponent getLine() {
            return line;
        }
    }

    public static class DialogButton extends DialogLine {
        private final Consumer<Player> consumer;

        public DialogButton(@NotNull TextComponent line, @NotNull Consumer<Player> consumer) {
            super(line);
            this.consumer = consumer;
        }

        public Consumer<Player> getConsumer() {
            return consumer;
        }
    }

    public static class DialogPageBreak extends DialogLine {
        public DialogPageBreak() {
            super(Component.newline());
        }
    }

    public static class DialogPageJump extends DialogLine {
        private final int page;

        public DialogPageJump(@NotNull TextComponent line, int page) {
            super(line);
            this.page = page;
        }

        public int getPage() {
            return page;
        }
    }

    public static class DialogSection extends DialogLine {
        private int page;

        public DialogSection(@NotNull TextComponent line) {
            super(line);
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    public static class DialogSectionJump extends DialogLine {
        private final String sectionIdentifier;

        public DialogSectionJump(@NotNull String sectionIdentifier, @NotNull TextComponent line) {
            super(line);
            this.sectionIdentifier = sectionIdentifier;
        }

        public String getSectionIdentifier() {
            return sectionIdentifier;
        }
    }

}
