package com.stat.tools.gadget.data;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
PlayerDao provides the methods that the rest of the app uses to interact with data in the user table.
 */

@Dao
public interface PlayerDao {

    @Query("SELECT * FROM player")
    public List<Player> getAllPlayers();

    @Insert
    public void insert(Player player);

    @Delete
    public void delete(Player player);

    @Update
    public void update(Player player);

    @Query("SELECT * FROM player WHERE id=:id")
    public Player getPlayerById(int id);
}

