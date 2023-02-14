package com.leaderbet.model;

import lombok.Data;

import java.util.List;

@Data
public class ImageConfig {
    private String defaultSize;  //selectedSize
    private List<String> selectedSizes;
}