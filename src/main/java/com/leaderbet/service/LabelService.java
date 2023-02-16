package com.leaderbet.service;

import com.leaderbet.Entity.Label;
import com.leaderbet.Entity.LabelGroup;
import com.leaderbet.model.LabelTreeModel;
import com.leaderbet.repository.LabelGroupsRepository;
import com.leaderbet.repository.LabelPairsRepository;
import com.leaderbet.repository.LabelRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelPairsRepository labelPairsRepository;
    private final LabelGroupsRepository labelGroupsRepository;

    public LabelService(LabelRepository labelRepository, LabelPairsRepository labelPairsRepository,
                        LabelGroupsRepository labelGroupsRepository) {
        this.labelRepository = labelRepository;
        this.labelPairsRepository = labelPairsRepository;
        this.labelGroupsRepository = labelGroupsRepository;
    }

    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    public Label add(Label label) {
        return labelRepository.save(label);
    }

    @Transactional
    public LabelTreeModel getAllForTree() {
        LabelTreeModel parent = new LabelTreeModel();
        List<LabelTreeModel> children = new ArrayList<>();

        getActiveRoots().forEach(root -> {
            var labelGroup = new LabelTreeModel();
            labelGroup.setText(root.getName());
            labelGroup.setLeaf(false);
            labelGroup.setXid(String.valueOf(root.getId()));

            var pairs = labelPairsRepository.findByDataId(root.getId());
            if (pairs != null) {
                List<LabelTreeModel> labels = new ArrayList<>();
                pairs.forEach(pair -> {
                    var l = labelRepository.findById(pair.getLabelId());
                    if (l.isPresent()) {
                        LabelTreeModel lastChild = new LabelTreeModel();
                        lastChild.setText(l.get().getName());
                        lastChild.setLeaf(true);
                        lastChild.setSort(pair.getSort());
                        lastChild.setXid(String.valueOf(l.get().getId()));
                        lastChild.setJoinId(String.valueOf(pair.getId()));
                        labels.add(lastChild);
                    }
                });
                labelGroup.setChildren(labels);
            }
            children.add(labelGroup);
        });
        var leafs = labelRepository.findAll();
        leafs.forEach(lf -> {
            LabelTreeModel lastChild = new LabelTreeModel();
            lastChild.setText(lf.getName());
            lastChild.setLeaf(true);
            lastChild.setXid(String.valueOf(lf.getId()));
            lastChild.setGameCount(!CollectionUtils.isEmpty(lf.getSlotPairs()) ? lf.getSlotPairs().size() : 0);
            children.add(lastChild);
        });
        parent.setChildren(children);

        return parent;
    }

    public List<LabelGroup> getActiveRoots() {
        return labelGroupsRepository.findAll();
    }
}
