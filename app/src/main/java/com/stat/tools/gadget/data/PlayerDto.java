package com.stat.tools.gadget.data;


/**
 * Data Transfer Object (DTO) for representing a key-value pair.
 */
public class PlayerDto {

    private String key;
    private Integer value;


    /**
     * Constructor to initialize the PlayerDto with a key and value.
     *
     * @param key   The key for this DTO.
     * @param value The value associated with the key.
     */
    public PlayerDto(String key, int value) {
        this.key = key;
        this.value = value;
    }

    // Getters and Setters
    public void setKey(String key){
        this.key = key;
    }

    public void setValue(int value){
        this.value = value;
    }

    public String getKey(){
        return key;
    }

    public int getValue(){
        return value;
    }


}
