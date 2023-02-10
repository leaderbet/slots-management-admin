package com.leaderbet.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leaderbet.Entity.Game;
import com.leaderbet.Entity.SlotPair;
import com.leaderbet.config.ConfigProps;
import com.leaderbet.repository.AbstractPairsRepository;
import com.leaderbet.repository.GameRepository;
import com.leaderbet.repository.ProviderRepository;
import com.leaderbet.repository.SlotPairsRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final ProviderRepository providerRepository;
    private final MinioService minioService;
    private final ConfigProps configProps;
    private final AbstractPairsRepository abstractPairsRepository;
    private final ObjectMapper objectMapper;
    private final SlotPairsRepository slotPairsRepository;

    public GameService(GameRepository gameRepository, ProviderRepository providerRepository, MinioService minioService, ConfigProps configProps,
                       AbstractPairsRepository abstractPairsRepository, ObjectMapper objectMapper, SlotPairsRepository slotPairsRepository) {
        this.gameRepository = gameRepository;
        this.providerRepository = providerRepository;
        this.minioService = minioService;
        this.configProps = configProps;
        this.abstractPairsRepository = abstractPairsRepository;
        this.objectMapper = objectMapper;
        this.slotPairsRepository = slotPairsRepository;
    }

    @Transactional
    public List<Game> search(String name, Integer providerId) {
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
        games.forEach(game -> {
            var labels = slotPairsRepository.findByDataId(game.getId());
            var labelIds = labels.stream().map(label -> label.getLabelId().toString()).toList();
            game.setLabelIds(labelIds);
        });
        return games;
    }

    @Transactional
    public Game add(String gameStr, MultipartFile image_1x1, MultipartFile image_1x2, MultipartFile image_2x1, MultipartFile image_2x2, MultipartFile bg) throws IOException {
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
