package com.leaderbet.controller;

import com.leaderbet.Entity.Label;
import com.leaderbet.model.LabelTreeModel;
import com.leaderbet.service.LabelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots_management/labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("/combo")
    public List<Label> getAll() {
        return labelService.getAll();
    }

//    @GetMapping
//    public List<LabelModel> getAllWithDetails() {
//        return labelService.getAllWithDetails();
//    }`

    @GetMapping("/for_tree")
    public LabelTreeModel getAllForTree() {
        return labelService.getAllForTree();
    }

    @PostMapping("/add")
    public Label add(@RequestBody Label label) {
        return labelService.add(label);
    }

    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable int id) {
        labelService.delete(id);
    }

}
