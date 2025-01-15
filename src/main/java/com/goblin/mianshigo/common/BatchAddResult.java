package com.goblin.mianshigo.common;

import lombok.Data;

import java.util.List;

@Data
public class BatchAddResult {
    private int total = 0;
    private int successCount = 0;
    private int failureCount = 0;
    private List<String> failureReasons;
}
