package com.stat.tools.gadget.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

/**
A Room entity includes fields for each column in the corresponding table in the database
 */
@Entity(tableName = "player")
@TypeConverters(Converters.class)  // Register the TypeConverter for the entity
public class Player {

    @PrimaryKey(autoGenerate = true)
    private int id;  // Add a primary key for Room

    @ColumnInfo(name = "name")
    private String name; // Changed 'Name' to 'name' for naming conventions

    @ColumnInfo(name = "logo")
    private String logo;

    // Indicator maps with default values
    @ColumnInfo(name = "indicator")
    private List<PlayerDto> indicator;

    @ColumnInfo(name = "victories")
    private List<PlayerDto> victories;

    @ColumnInfo(name = "defeats")
    private List<PlayerDto> defeats;

    @ColumnInfo(name = "other_metrics")
    private List<PlayerDto> otherMetrics;

    // Constructor with parameters
    public Player(String name, String logo, List<PlayerDto> victories, List<PlayerDto> defeats, List<PlayerDto> otherMetrics,
                  List<PlayerDto> indicator) {
        this.name = name;
        this.logo = logo;
        this.victories = victories;
        this.defeats = defeats;
        this.otherMetrics = otherMetrics;
        this.indicator = indicator;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for logo
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    // Getter and Setter for victories
    public List<PlayerDto> getVictories() {
        return victories;
    }

    public void setVictories(List<PlayerDto> victories) {
        this.victories = victories;
    }

    // Getter and Setter for defeats
    public List<PlayerDto> getDefeats() {
        return defeats;
    }

    public void setDefeats(List<PlayerDto> defeats) {
        this.defeats = defeats;
    }

    // Getter and Setter for otherMetrics
    public List<PlayerDto> getOtherMetrics() {
        return otherMetrics;
    }

    public void setOtherMetrics(List<PlayerDto> otherMetrics) {
        this.otherMetrics = otherMetrics;
    }

    public List<PlayerDto> getIndicator() {
        return indicator;
    }

    public void setIndicator(List<PlayerDto> indicator) {
        this.indicator = indicator;
    }

    // Optional: ToString method for debugging
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", victories=" + victories +
                ", defeats=" + defeats +
                ", otherMetrics=" + otherMetrics +
                ", winsIndicator=" + indicator +
                '}';
    }
}
