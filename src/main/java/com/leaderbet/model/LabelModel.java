package com.leaderbet.model;

import lombok.Data;

import java.util.List;

@Data
public class LabelModel {
    private Integer xid;
    private String labelName;
    private List<String> labels;
    private List<String> parentLabels;
    private List<String> parentLabelIds;
}
