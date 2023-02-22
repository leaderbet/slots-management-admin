package com.leaderbet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leaderbet.Entity.Game;
import com.leaderbet.model.GameWrapper;
import com.leaderbet.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/slots_management/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> search(@RequestParam(required = false) Set<Integer> labelIds,
                             @RequestParam(defaultValue = "false") Boolean withoutPhoto,
                             @RequestParam(defaultValue = "false") Boolean uncategorized) {
        return gameService.search(labelIds, withoutPhoto, uncategorized);
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

    @GetMapping("{id}/images")
    public Map<String, String> getGameImages(@PathVariable int id) throws JsonProcessingException {
        return gameService.getGameImages(id);
    }

    @PostMapping(value = "{id}/add_image"/*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE*/)
    public GameWrapper changeImage(@PathVariable int id,
                                   @RequestParam("file_image") MultipartFile file,
                                   @RequestParam(required = false) String size,
                                   @RequestParam(required = false) String s) throws IOException {
        return gameService.changeImage(id, file, s, size);
    }

    @PostMapping(value = "{id}/change_image_size")
    public Game changeImageSelectedSize(@PathVariable int id,
                                        @RequestParam(required = false) String size) throws IOException {
        return gameService.changeImageSelectedSize(id, size);
    }

    @PostMapping("/{id}/add_sort")
    public void setSort(@PathVariable int id,
                        @RequestParam int labelId,
                        @RequestParam(defaultValue = "0.00") double sort) {
        gameService.setSort(id, labelId, sort);
    }

    @PutMapping("/{id}/change_status")
    public void changeProviderStatus(@PathVariable int id,
                                     @RequestParam int status) {
        gameService.changeGameStatus(id, status);
    }

}
