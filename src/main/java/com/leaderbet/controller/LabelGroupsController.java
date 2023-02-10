package com.leaderbet.controller;

import com.leaderbet.Entity.LabelGroup;
import com.leaderbet.service.LabelGroupsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/slots_management/label_groups")
public class LabelGroupsController {

    private final LabelGroupsService labelGroupsService;

    public LabelGroupsController(LabelGroupsService labelGroupsService) {
        this.labelGroupsService = labelGroupsService;
    }

    @PostMapping("/add")
    public LabelGroup add(@RequestBody LabelGroup labelGroup) {
        return labelGroupsService.add(labelGroup);
    }
}
