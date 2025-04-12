package ru.sarahbot.sarah.file.service;

public class ExtensionUtils {
    public static String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> null;
        };
    }
}
