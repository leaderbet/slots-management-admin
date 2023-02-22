package com.leaderbet.controller;

import com.leaderbet.Entity.LabelPair;
import com.leaderbet.service.LabelPairsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots_management/label_pairs")
public class LabelPairsController {
    private final LabelPairsService labelPairsService;

    public LabelPairsController(LabelPairsService labelPairsService) {
        this.labelPairsService = labelPairsService;
    }

    @GetMapping
    public List<LabelPair> getAll() {
        return labelPairsService.getAll();
    }

    @PostMapping("add")
    public LabelPair add(@RequestBody LabelPair labelPair) {
        return labelPairsService.add(labelPair);
    }

    @PostMapping("/{id}/change_sort")
    public LabelPair changeLabelPairSort(@PathVariable int id,
                                         @RequestParam double sort) {
        return labelPairsService.changeLabelPairSort(id, sort);

    }

    @DeleteMapping("/{id}/delete")
    public void deleteLabelPair(@PathVariable int id){
        labelPairsService.deleteLabelPair(id);
    }
}
