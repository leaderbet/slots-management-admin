package com.leaderbet.service;

import com.leaderbet.Entity.Label;
import com.leaderbet.Entity.LabelGroup;
import com.leaderbet.Entity.LabelPair;
import com.leaderbet.model.LabelTreeModel;
import com.leaderbet.repository.LabelGroupsRepository;
import com.leaderbet.repository.LabelPairsRepository;
import com.leaderbet.repository.LabelRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;


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

    public Label edit(Integer id, Label label) {
        return labelRepository.findById(id)
                .map(existingLabel -> {
                    var LabelToUpdate = new Label(
                            existingLabel.getId(),
                            label.getName());
                    return labelRepository.save(LabelToUpdate);
                })
                .orElseThrow();
    }

    public void delete(int id) {
        labelRepository.deleteById(id);
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
            children.add(lastChild);
        });

        parent.setChildren(children);

        return parent;
    }

    public List<LabelGroup> getActiveRoots() {
        return labelGroupsRepository.findAll();
    }

    public List<Label> getActiveChildren(int rootId) {
        List<Label> children = new ArrayList<>();
        var pair = labelPairsRepository.findByDataId(rootId);
        pair.forEach(p -> children.add(labelRepository.findById(p.getLabelId()).get()));
        return children;
    }


//    @Transactional
//    public List<LabelModel> getAllWithDetails() {

//        return labelPairsRepository.findAll().stream()
//                .map(labelPair -> {
//                    LabelModel labelModel = new LabelModel();
//                    labelModel.setXid(labelPair.getId());
//                    Label label = labelPair.getLabel();
//                    Label source = labelPair.getSource();
//                    labelModel.setLabelName(label.getName());
//                    labelModel.setParentLabels(List.of(source.getName()));
//                    labelModel.setParentLabelIds(List.of(source.getId().toString()));
//                    return labelModel;
//                })
//                .toList();



/*
        return getAll().stream().map(label -> {
            Stream<Label> a = labelPairsRepository.findBySourceId(label.getId()).stream()
                    .filter(pair -> Objects.equals(pair.getDataType(), "LABEL"))
                    .map(pair -> labelRepository.findById(pair.getLabelId()).orElse(null))
                    .filter(Objects::nonNull);
            return new AbstractMap.SimpleEntry<>(label, a);
        }).map(entry -> entry.getValue().collect(
                Collectors.teeing(
                        Collectors.mapping(Label::getName, Collectors.toList()),
                        Collectors.mapping(label -> String.valueOf(label.getId()), Collectors.toList()),
                        (names, ids) -> {
                            Label parent = entry.getKey();
                            var labelModel = new LabelModel();
                            labelModel.setLabelName(parent.getName());
                            labelModel.setXid(parent.getId());
                            labelModel.setParentLabels(names);
                            labelModel.setParentLabelIds(ids);
                            return labelModel;
                        }
                ))).toList();*/
//    }

//    public List<LabelModel> getAllWithDetails() {
//        List<Label> labels = getAll();
//        return labels.stream().map(label -> {
//
//            var labelModel = new LabelModel();
//            labelModel.setLabelName(label.getName());
//            labelModel.setXid(label.getId());
//
//            var labelPairs = labelPairsRepository.findBySourceId(label.getId());
//
//            if (!CollectionUtils.isEmpty(labelPairs)) {
//                List<String> parentLabels = new ArrayList<>();
//                List<String> parentLabelIds = new ArrayList<>();
//
//                labelPairs.stream()
//                        .filter(pair -> Objects.equals(pair.getDataType(), "LABEL"))
//                        .forEach(pair -> {
//                            parentLabels.add(labelRepository.findById(pair.getLabelId()).get().getName());
//                            parentLabelIds.add(String.valueOf(pair.getLabelId()));
//                        });
//
//                labelModel.setParentLabels(parentLabels);
//                labelModel.setParentLabelIds(parentLabelIds);
//            }
//            return labelModel;
//        }).collect(Collectors.toList());
//    }
}
