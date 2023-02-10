package com.leaderbet.controller;

import com.leaderbet.Entity.Game;
import com.leaderbet.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/slots_management/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> search(@RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer providerId) {
        return gameService.search(name, providerId);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Game add(@RequestParam String gameStr,
                    @RequestParam(required = false) MultipartFile image_1x1,
                    @RequestParam(required = false) MultipartFile image_1x2,
                    @RequestParam(required = false) MultipartFile image_2x1,
                    @RequestParam(required = false) MultipartFile image_2x2,
                    @RequestParam(required = false) MultipartFile bg) throws IOException {
        return gameService.add(gameStr, image_1x1, image_1x2, image_2x1, image_2x2, bg);
    }

    @PutMapping("/{id}/edit")
    public Game edit(@PathVariable int id,
                     @RequestBody Game game) {
        game.setId(id);
        return gameService.edit(game);
    }

    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable int id) {
        gameService.delete(id);
    }
}
