package com.racecondition.controller;

import com.racecondition.dto.ApiResponse;
import com.racecondition.dto.HealthData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @Value("${spring.application.name:java-race-condition}")
    private String serviceName;

    @GetMapping("/health")
    public ApiResponse<HealthData> health() {
        HealthData data = new HealthData("UP", serviceName, System.currentTimeMillis());
        return ApiResponse.success(data);
    }
}
