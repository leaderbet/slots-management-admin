package com.leaderbet.service;

import com.leaderbet.Entity.LabelGroup;
import com.leaderbet.repository.LabelGroupsRepository;
import org.springframework.stereotype.Service;

@Service
public class LabelGroupsService {
    private final LabelGroupsRepository labelGroupsRepository;

    public LabelGroupsService(LabelGroupsRepository labelGroupsRepository) {
        this.labelGroupsRepository = labelGroupsRepository;
    }

    public LabelGroup add(LabelGroup labelGroup) {
        return labelGroupsRepository.save(labelGroup);
    }

}
