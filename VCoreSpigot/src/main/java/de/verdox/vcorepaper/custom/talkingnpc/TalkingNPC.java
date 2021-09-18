/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.talkingnpc;

import com.google.common.collect.ImmutableMap;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import de.verdox.vcorepaper.custom.gui.book.BookGUI;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.09.2021 22:47
 */
public class TalkingNPC extends VCoreEntity {
    public static final String KEY_COMPOUND = "talkingNPC";
    public static final String KEY_TYPE = "type";
    public static final String TALKING_TEXT = "talking";
    public static final String NPC_NAME = "name";

    private final Map<TextComponent, Consumer<Player>> buttons = new LinkedHashMap<>();

    public TalkingNPC(@NotNull Entity entity, @NotNull CustomEntityManager customEntityManager) {
        super(entity, customEntityManager);
        if (getType().isEmpty() || getType().equals("none"))
            setType(customEntityManager.getTalkingNPCService().getIdentifier(getClass()));
    }

    public void updateNameTag() {
        String name = getName();
        if (name.isEmpty())
            getDataHolder().setCustomNameVisible(false);
        else {
            getDataHolder().setCustomNameVisible(true);
            getDataHolder().setCustomName(ChatColor.translateAlternateColorCodes('&', name));
        }
    }

    public boolean verify() {
        return toNBTHolder().getPersistentDataContainer().hasKey(KEY_COMPOUND);
    }

    public void initialize() {
        setType(getCustomDataManager().getTalkingNPCService().getIdentifier(getClass()));
        toNBTHolder().getPersistentDataContainer().addCompound(KEY_COMPOUND);
    }

    public void setNPCName(@NotNull String npcName) {
        toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).setString(NPC_NAME, npcName);
    }

    String getType() {
        if (!toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).hasKey(KEY_TYPE))
            return "";
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).getString(KEY_TYPE);
    }

    private void setType(@NotNull String identifier) {
        toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).setString(KEY_TYPE, identifier);
    }

    public String getName() {
        if (!toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).hasKey(NPC_NAME))
            return "";
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).getString(NPC_NAME);
    }

    public String getTalking() {
        if (!toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).hasKey(TALKING_TEXT))
            return "";
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).getString(TALKING_TEXT);
    }

    public void setTalking(@NotNull String talking) {
        toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).setString(TALKING_TEXT, talking);
    }

    protected void addStandardButtons(@NotNull Player buttonPlayer) {
        TextComponent textComponent = Component
                .text("Gespräch beenden")
                .color(TextColor.fromHexString("#3B3B3B"))
                .hoverEvent(HoverEvent.showText(Component.text("Klicke mich").color(TextColor.fromHexString("#ffbfa0"))));
        addButton(textComponent
                , player -> VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.ACTION_BAR, "&7Du wendest dich ab..."));

    }

    public TextComponent addButton(@NotNull TextComponent textComponent, @NotNull Consumer<Player> consumer) {
        buttons.put(textComponent, consumer);
        return textComponent;
    }

    public BookGUI createBookGUI(@NotNull Player player) {
        addStandardButtons(player);
        BookGUI bookGUI = new BookGUI(VCorePaper.getInstance(), player);

        List<TextComponent> responsiveButtons = buildResponsiveButtons(bookGUI);
        bookGUI.provideBook(() -> {
            String talking = getTalking();
            Book.Builder builder = Book.builder();
            TextComponent textComponent = Component.text("");
            String name = getName();
            if (!name.isEmpty()) {
                textComponent = textComponent.append(Component.text(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name))).decoration(TextDecoration.UNDERLINED, true).color(TextColor.fromHexString("#44ffd2")))
                        .append(Component.newline())
                        .append(Component.newline());
            }
            if (!talking.isEmpty()) {
                List<String> lines = VCoreUtil.BukkitUtil.getBukkitBookUtil().getLines(getTalking());

                for (int i = 0; i < lines.size(); i++) {
                    String line = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', lines.get(i)));
                    textComponent = textComponent.append(Component.newline().append(Component.text(line)).color(TextColor.fromHexString("#774936")));

                    if (i != 0 && i % 12 == 0) {
                        builder.addPage(textComponent);
                        textComponent = Component.text("");
                    }
                    // Page is full
                    if (i == lines.size() - 1)
                        textComponent = addButtonsToEnd(textComponent, responsiveButtons);
                }
            } else
                textComponent = addButtonsToEnd(textComponent, responsiveButtons);
            builder.addPage(textComponent);
            return builder.build();
        });
        return bookGUI;
    }

    private TextComponent addButtonsToEnd(@NotNull TextComponent page, @NotNull List<TextComponent> responsiveButtons) {
        for (int j = 0; j < responsiveButtons.size(); j++) {
            TextComponent responsiveButton = responsiveButtons.get(j);
            page = page.append(Component.newline()).append(Component.newline().append(Component.text("<" + (j + 1) + "> ")).color(responsiveButton.color()).append(responsiveButton));
        }
        return page;
    }

    private List<TextComponent> buildResponsiveButtons(@NotNull BookGUI bookGUI) {
        return getButtons().entrySet()
                .stream()
                .map(textComponentConsumerEntry -> {
                    TextComponent textComponent = bookGUI.createResponsiveCallbackText(textComponentConsumerEntry.getKey(), textComponentConsumerEntry.getValue());
                    textComponent = textComponent.hoverEvent(HoverEvent.showText(Component.text("Klicke mich").color(TextColor.fromHexString("#ffbfa0"))));
                    return textComponent;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<TextComponent, Consumer<Player>> getButtons() {
        return ImmutableMap.copyOf(buttons);
    }
}
