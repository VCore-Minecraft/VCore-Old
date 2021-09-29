/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.talkingnpc;

import com.google.common.collect.ImmutableMap;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import de.verdox.vcorepaper.custom.gui.book.DialogGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

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
    public static final String TRADING_DISABLED = "tradingDisabled";

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

    public boolean isTradingDisabled() {
        if (!toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).hasKey(TALKING_TEXT))
            return false;
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).getBoolean(TRADING_DISABLED);
    }

    public void setTradingDisabled(boolean disabled) {
        toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).setBoolean(TRADING_DISABLED, disabled);
    }

    public String getTalking() {
        if (!toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).hasKey(TALKING_TEXT))
            return "";
        return toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).getString(TALKING_TEXT);
    }

    public void setTalking(@NotNull String talking) {
        toNBTHolder().getPersistentDataContainer().getOrCreateCompound(KEY_COMPOUND).setString(TALKING_TEXT, talking);
    }

    public void openDialogGUI(@NotNull Player player) {
        addStandardButtons(player);
        DialogGUI dialogGUI = new DialogGUI(player, getName(), getTalking());
        buttons.forEach(dialogGUI::addButton);
        dialogGUI.openDialog();
    }

    protected void addStandardButtons(@NotNull Player buttonPlayer) {
        Villager villager = (Villager) getDataHolder();
        if (VCorePaper.getInstance().getNmsManager().getNMSEntityHandler().getOffers(villager) > 0 && !isTradingDisabled()) {
            TextComponent tradingComponent = Component
                    .text("Handeln")
                    .color(TextColor.fromHexString("#3B3B3B"))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicke mich").color(TextColor.fromHexString("#ffbfa0"))));
            addButton(tradingComponent
                    , player -> {
                        VCorePaper.getInstance().getNmsManager().getNMSEntityHandler().openTradingGUI(villager, buttonPlayer);
                    });
        }

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

    public Map<TextComponent, Consumer<Player>> getButtons() {
        return ImmutableMap.copyOf(buttons);
    }
}
