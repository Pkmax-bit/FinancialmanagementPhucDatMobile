package com.example.financialmanagement.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for emoji management
 */
public class EmojiHelper {
    
    // Emoji categories
    public static final int CATEGORY_RECENT = 0;
    public static final int CATEGORY_SMILEYS = 1;
    public static final int CATEGORY_PEOPLE = 2;
    public static final int CATEGORY_NATURE = 3;
    public static final int CATEGORY_FOOD = 4;
    public static final int CATEGORY_ACTIVITIES = 5;
    public static final int CATEGORY_TRAVEL = 6;
    public static final int CATEGORY_OBJECTS = 7;
    public static final int CATEGORY_SYMBOLS = 8;
    public static final int CATEGORY_FLAGS = 9;
    
    // Popular emojis by category
    private static final String[] SMILEYS = {
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
        "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
        "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
        "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤥",
        "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮"
    };
    
    private static final String[] PEOPLE = {
        "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤏", "✌️", "🤞", "🤟",
        "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", "👎",
        "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏",
        "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🧠", "🦷"
    };
    
    private static final String[] NATURE = {
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
        "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆",
        "🌲", "🌳", "🌴", "🌱", "🌿", "☘️", "🍀", "🌾", "🌵", "🌹",
        "🌺", "🌻", "🌷", "🌸", "💐", "🍄", "🌰", "🎄", "✨", "🔥"
    };
    
    private static final String[] FOOD = {
        "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈",
        "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
        "🥬", "🥒", "🌶", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠", "🥐",
        "🥖", "🍞", "🥨", "🥯", "🧇", "🥞", "🧈", "🍕", "🍔", "🌭",
        "🌮", "🌯", "🥙", "🍳", "🥘", "🍲", "🥣", "🥗", "🍿", "🧂"
    };
    
    private static final String[] ACTIVITIES = {
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
        "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "⛳", "🏹", "🎣", "🥊",
        "🥋", "🎽", "⛸", "🥌", "🎿", "⛷", "🏂", "🏋️", "🤺", "🤼",
        "🤸", "🤾", "⛹️", "🤺", "🧘", "🏇", "🏊", "🤽", "🚣", "🧗"
    };
    
    private static final String[] TRAVEL = {
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐",
        "🚚", "🚛", "🚜", "🏍", "🛵", "🚲", "🛴", "🚏", "🛣", "⛽",
        "✈️", "🚁", "🚂", "🚊", "🚝", "🚄", "🚅", "🚆", "🚇", "🚈",
        "🚉", "🚞", "🚋", "🚃", "🚟", "🚠", "🚡", "🛶", "⛵", "🚤"
    };
    
    private static final String[] OBJECTS = {
        "⌚", "📱", "📲", "💻", "⌨️", "🖥", "🖨", "🖱", "🖲", "🕹",
        "🗜", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥",
        "📽", "🎞", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙", "🎚",
        "🎛", "⏱", "⏲", "⏰", "🕰", "⌛", "⏳", "📡", "🔋", "🔌"
    };
    
    private static final String[] SYMBOLS = {
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
        "✝️", "☪️", "🕉", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐",
        "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐"
    };
    
    private static final String[] FLAGS = {
        "🏁", "🚩", "🎌", "🏴", "🏳️", "🏳️‍🌈", "🏴‍☠️", "🇻🇳", "🇺🇸", "🇬🇧",
        "🇨🇳", "🇯🇵", "🇰🇷", "🇫🇷", "🇩🇪", "🇮🇹", "🇪🇸", "🇷🇺", "🇧🇷", "🇨🇦"
    };
    
    /**
     * Get emojis for a category
     */
    public static List<String> getEmojis(int category) {
        String[] emojis;
        switch (category) {
            case CATEGORY_SMILEYS:
                emojis = SMILEYS;
                break;
            case CATEGORY_PEOPLE:
                emojis = PEOPLE;
                break;
            case CATEGORY_NATURE:
                emojis = NATURE;
                break;
            case CATEGORY_FOOD:
                emojis = FOOD;
                break;
            case CATEGORY_ACTIVITIES:
                emojis = ACTIVITIES;
                break;
            case CATEGORY_TRAVEL:
                emojis = TRAVEL;
                break;
            case CATEGORY_OBJECTS:
                emojis = OBJECTS;
                break;
            case CATEGORY_SYMBOLS:
                emojis = SYMBOLS;
                break;
            case CATEGORY_FLAGS:
                emojis = FLAGS;
                break;
            case CATEGORY_RECENT:
            default:
                // Return recent emojis (for now, return popular ones)
                return getRecentEmojis();
        }
        return Arrays.asList(emojis);
    }
    
    /**
     * Get recent emojis (simplified - could be stored in SharedPreferences)
     */
    private static List<String> getRecentEmojis() {
        return Arrays.asList(
            "😀", "❤️", "👍", "😂", "🎉", "🔥", "💯", "👏", "🙏", "😍",
            "💪", "✨", "🎊", "🥳", "😎", "🤔", "👌", "✅", "📱", "💼"
        );
    }
    
    /**
     * Get all emojis (for search)
     */
    public static List<String> getAllEmojis() {
        List<String> all = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {  // Skip RECENT
            all.addAll(getEmojis(i));
        }
        return all;
    }
}

