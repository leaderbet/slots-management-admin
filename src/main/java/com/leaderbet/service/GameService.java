package com.leaderbet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leaderbet.Entity.Game;
import com.leaderbet.Entity.Label;
import com.leaderbet.Entity.SlotPair;
import com.leaderbet.config.ConfigProps;
import com.leaderbet.model.ImageConfig;
import com.leaderbet.repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
    public List<Game> search(String name, Integer providerId, Set<Integer> labelIds) {
        List<Game> games = gameRepository.findAll((root, query, cb) -> {

            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(name)) {
                predicate = cb.and(predicate, cb.equal(root.get("name"), name));
            }
            if (providerId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("providerId"), providerId));
            }
            return predicate;
        });

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
            var labels = slotPairsRepository.findByDataId(game.getId());
            var ids = labels.stream().map(label -> label.getLabelId().toString()).toList();
            game.setLabelIds(ids);
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
}
