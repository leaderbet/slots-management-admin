package com.leaderbet.model;

import lombok.Data;

import java.util.List;

@Data
public class ImageConfig {
    private String selectedSize;
    private List<String> possibleSizes;
}