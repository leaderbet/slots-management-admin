package com.leaderbet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leaderbet.Entity.Game;
import com.leaderbet.Entity.Label;
import com.leaderbet.Entity.SlotPair;
import com.leaderbet.config.ConfigProps;
import com.leaderbet.model.GameWrapper;
import com.leaderbet.model.ImageConfig;
import com.leaderbet.repository.*;
import jakarta.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final MinioService minioService;
    private final ConfigProps configProps;
    private final ObjectMapper objectMapper;
    private final SlotPairsRepository slotPairsRepository;
    private final LabelRepository labelRepository;

    public GameService(GameRepository gameRepository, MinioService minioService, ConfigProps configProps,
                       ObjectMapper objectMapper, SlotPairsRepository slotPairsRepository,
                       LabelRepository labelRepository) {
        this.gameRepository = gameRepository;
        this.minioService = minioService;
        this.configProps = configProps;
        this.objectMapper = objectMapper;
        this.slotPairsRepository = slotPairsRepository;
        this.labelRepository = labelRepository;
    }

    private Game getById(int id) {
        var g = gameRepository.findById(id);
        if (g.isEmpty()) {
            throw new NoSuchElementException();
        }
        return g.get();
    }

    @Transactional
    public List<Game> search(Set<Integer> labelIds) {
        List<Game> games = gameRepository.findAll();
        if (!CollectionUtils.isEmpty(labelIds)) {
            Set<Label> labels = labelRepository.findByIdWithGames(labelIds);
            List<Game> finalGames = games;

            Map<Game, Set<Integer>> a = labels.stream()
                    .flatMap(label -> label.getSlotPairs().stream())
                    .filter(slotPair -> finalGames.contains(slotPair.getGame()))
                    .collect(Collectors.groupingBy(SlotPair::getGame, Collectors.mapping(SlotPair::getLabelId, Collectors.toSet())));

            games = a.entrySet().stream()
                    .filter(gameSetEntry -> gameSetEntry.getValue().containsAll(labelIds))
                    .map(Map.Entry::getKey)
                    .toList();
        }
        games.forEach(game -> {
            List<SlotPair> labels = slotPairsRepository.findByDataId(game.getId());
            var ids = labels.stream().map(label -> label.getLabelId().toString()).toList();
            game.setLabelIds(ids);

            if (labelIds.size() == 1) {
                SlotPair lp = slotPairsRepository.findByDataIdAndLabelId(game.getId(), labelIds.iterator().next());
                game.setSort(lp.getSort());
            }
        });

        return games;
    }

    @Transactional
    public Game add(String gameStr, MultipartFile image_1x1, MultipartFile image_1x2, MultipartFile image_2x1,
                    MultipartFile image_2x2, MultipartFile bg) throws IOException {
        Game game = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(gameStr, Game.class);

        var savedGame = gameRepository.save(game);


        if (image_1x1 != null) savePhoto(image_1x1, String.valueOf(savedGame.getId()), "1x1");
        if (image_1x2 != null) savePhoto(image_1x2, String.valueOf(savedGame.getId()), "1x2");
        if (image_2x1 != null) savePhoto(image_2x1, String.valueOf(savedGame.getId()), "2x1");
        if (image_2x2 != null) savePhoto(image_2x2, String.valueOf(savedGame.getId()), "2x2");
        if (bg != null) savePhoto(bg, String.valueOf(savedGame.getId()), "bg");

        return savedGame;
    }

    private void savePhoto(MultipartFile photo, String slotId, String size) throws IOException {
        String fullName = String.format("%s_%s.%s", slotId, size, "png");

        if (photo != null) {
            minioService.putObject(configProps.getMinioBucket(), fullName, "image/*", photo.getBytes());
        }
    }

    private String getImage(byte[] image) {
        return image != null ? Base64.getEncoder().encodeToString(image) : null;
    }

    public Map<String, String> getImages(Game game) throws JsonProcessingException {
        Map<String, String> imgMap = new HashMap<>();

        var conf = game.getImageConfig();
        var obj = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(conf, ImageConfig.class);

        List<String> possibleSizes = obj.getPossibleSizes();
        if (!CollectionUtils.isEmpty(possibleSizes)) {
            possibleSizes.forEach(size -> {
                String fullName = String.format("%s_%s.%s", game.getId(), size, "png");
                var image = getImage(minioService.getObject(configProps.getMinioBucket(), fullName));
                if (image != null) {
                    imgMap.put(size, image);
                }
            });
        }

        return imgMap;
    }

    public String getImage(int id, String size) {
        String fullName = String.format("%s_%s.%s", id, size, "png");
        return getImage(minioService.getObject(configProps.getMinioBucket(), fullName));
    }

    public void delete(Integer id) {
        var game = gameRepository.findById(id);
        game.ifPresent(p -> p.setDeletedAt(LocalDateTime.now()));
    }

    @Transactional
    public Game edit(Game game) {
        if (!CollectionUtils.isEmpty(game.getLabelIds())) {
            game.getLabelIds().forEach(labelId -> {
                var labelPair = slotPairsRepository.findByDataIdAndLabelId(game.getId(), Integer.parseInt(labelId));
                if (labelPair != null) slotPairsRepository.delete(labelPair);

                var label = new SlotPair(game.getId(), Integer.parseInt(labelId));
                slotPairsRepository.save(label);
            });
        }
        return gameRepository.save(game);
    }

    public Map<String, String> getGameImages(int id) throws JsonProcessingException {
        var game = getById(id);
        return getImages(game);
    }

    @Transactional
    public GameWrapper changeImage(int id, MultipartFile file, String s, String size) throws IOException {
        savePhoto(file, String.valueOf(id), s);
        var game = getById(id);
        var imageConfig = game.getImageConfig();
        var imageConfigObj = objectMapper.readValue(imageConfig, ImageConfig.class);
        var possibleSizes = imageConfigObj.getPossibleSizes();
        if (!possibleSizes.contains(s)) {
            possibleSizes.add(s);
            var jo = convertToJson(size, possibleSizes);
            game.setImageConfig(String.valueOf(jo));
            gameRepository.save(game);
        }
        return new GameWrapper(true, game);
    }

    private JSONObject convertToJson(String selectedSize, List<String> possibleSizes) {
        JSONObject jo = new JSONObject();
        jo.put("selectedSize", selectedSize);
        jo.put("possibleSizes", possibleSizes);

        return jo;
    }

    @Transactional
    public Game changeImageSelectedSize(int id, String size) throws JsonProcessingException {
        var game = getById(id);
        var imageConfig = game.getImageConfig();
        var imageConfigObj = objectMapper.readValue(imageConfig, ImageConfig.class);

        var jo = convertToJson(size, imageConfigObj.getPossibleSizes());
        game.setImageConfig(String.valueOf(jo));
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public void setSort(int dataId, int labelId, double sort) {
        SlotPair oldSlotPair = slotPairsRepository.findByDataIdAndLabelId(dataId, labelId);
        if (oldSlotPair != null) slotPairsRepository.delete(oldSlotPair);

        SlotPair slotPair = new SlotPair(dataId, labelId, sort);
        slotPairsRepository.save(slotPair);
    }
}
