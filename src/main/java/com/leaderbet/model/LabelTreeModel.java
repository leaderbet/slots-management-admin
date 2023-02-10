package com.leaderbet.model;

import lombok.Data;

import java.util.List;

@Data
public class LabelTreeModel {
    private String xid;
    private String joinId;
    private String text;
    private Boolean leaf;
    private Double sort;
    private List<LabelTreeModel> children;
}
