package com.leaderbet.service;

import com.leaderbet.Entity.LabelPair;
import com.leaderbet.repository.LabelPairsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LabelPairsService {
    private final LabelPairsRepository labelPairsRepository;

    public LabelPair getById(int id) {
        Optional<LabelPair> labelPair = labelPairsRepository.findById(id);
        if (labelPair.isEmpty())
            throw new NoSuchElementException();
        return labelPair.get();
    }

    public LabelPairsService(LabelPairsRepository labelPairsRepository) {
        this.labelPairsRepository = labelPairsRepository;
    }

    public List<LabelPair> getAll() {
        return labelPairsRepository.findAll();
    }

    public LabelPair add(LabelPair labelPair) {
        var label = labelPairsRepository.findByDataIdAndLabelId(labelPair.getDataId(), labelPair.getLabelId());
        if (label != null) labelPairsRepository.delete(label);
        return labelPairsRepository.save(labelPair);
    }

    @Transactional
    public LabelPair changeLabelPairSort(int id, double sort) {
        var labelPair = getById(id);
        labelPair.setSort(sort);
        return labelPair;
    }

}
