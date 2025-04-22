package me.zombieman.nightvisionplus.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorUtils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Converts a string to a component using MiniMessage.
     *
     * @param message: The message String to convert.
     * @return:        The converted component.
     */
    public static @NotNull Component deserialize(String message) {
        if (message == null) return Component.empty();

        return miniMessage.deserialize(message);
    }

    /**
     * Converts a component to a string using MiniMessage.
     *
     * @param component: The component to convert.
     * @return:         The converted string.
     */
    public static @NotNull String serialize(Component component) {
        if (component == null) {
            return "";
        }

        return miniMessage.serialize(component);
    }

    /**
     * Converts a list of String to a list of component using MiniMessage.
     *
     * @param messages: The list of messages to convert.
     * @return:         The converted list of components.
     */
    public static @NotNull List<Component> deserializeList(List<String> messages) {
        if (messages == null) {
            return List.of();
        }

        if (!messages.isEmpty()) {
            return messages.stream()
                    .map(ColorUtils::deserialize)
                    .toList();
        }

        return List.of();
    }

    /**
     * Converts a list of component to a list of String using MiniMessage.
     *
     * @param components: The list of components to convert.
     * @return:          The converted list of strings.
     */
    public static @NotNull List<String> serializeList(List<Component> components) {
        if (components == null) {
            return List.of();
        }

        if (!components.isEmpty()) {
            return components.stream()
                    .map(ColorUtils::serialize)
                    .toList();
        }

        return List.of();
    }
}