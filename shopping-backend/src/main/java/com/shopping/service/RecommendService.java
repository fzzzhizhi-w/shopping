package com.shopping.service;

import com.shopping.common.Result;

public interface RecommendService {

    Result<String> getAiRecommendations(Long userId, String category, String budget);
}
