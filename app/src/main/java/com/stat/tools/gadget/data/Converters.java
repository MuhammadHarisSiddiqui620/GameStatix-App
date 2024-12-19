package com.stat.tools.gadget.data;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * TypeConverters for converting between database types and custom types using Gson.
 */
public class Converters {

    private static final Gson gson = new Gson();

    /**
     * Convert a List of PlayerDto objects to a JSON String.
     *
     * @param playerDtoList The list of PlayerDto to be converted.
     * @return A JSON String representing the list, or null if the list is null.
     */
    @TypeConverter
    public static String fromPlayerDtoList(List<PlayerDto> playerDtoList) {
        if (playerDtoList == null) {
            return null;
        }
        return gson.toJson(playerDtoList);
    }

    /**
     * Convert a JSON String back to a List of PlayerDto objects.
     *
     * @param playerDtoString The JSON String to be converted.
     * @return A List of PlayerDto, or null if the string is null.
     */
    @TypeConverter
    public static List<PlayerDto> toPlayerDtoList(String playerDtoString) {
        if (playerDtoString == null) {
            return null;
        }
        Type type = new TypeToken<List<PlayerDto>>() {}.getType();
        return gson.fromJson(playerDtoString, type);
    }

    /**
     * Convert a List of Strings to a single concatenated String.
     *
     * @param list The list of Strings to be converted.
     * @return A concatenated String with elements separated by commas, or null if the list is null.
     */
    @TypeConverter
    public static String fromPlayerDto(PlayerDto playerDto) {
        if (playerDto == null) {
            return null;
        }
        return gson.toJson(playerDto);
    }

    // Convert JSON String back to PlayerDto
    @TypeConverter
    public static PlayerDto toPlayerDto(String playerDtoString) {
        if (playerDtoString == null) {
            return null;
        }
        return gson.fromJson(playerDtoString, PlayerDto.class);
    }

    // Convert List<String> to a single concatenated String
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        return String.join(",", list);
    }

    /**
     * Convert a concatenated String back to a List of Strings.
     *
     * @param concatenated The concatenated String to be converted.
     * @return A List of Strings, or null if the string is null.
     */    @TypeConverter
    public static List<String> fromString(String concatenated) {
        if (concatenated == null) {
            return null;
        }
        return Arrays.asList(concatenated.split(","));
    }
}
