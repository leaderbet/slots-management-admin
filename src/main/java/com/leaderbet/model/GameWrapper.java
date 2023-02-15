package com.leaderbet.model;

import com.leaderbet.Entity.Game;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameWrapper {
    private Boolean success;
    private Game game;
}