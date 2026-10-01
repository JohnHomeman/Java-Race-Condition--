package com.racecondition.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("健康檢查探針成功")
    void testHealthSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.status", is("UP")))
                .andExpect(jsonPath("$.data.service", is("java-race-condition")))
                .andExpect(jsonPath("$.data.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("不支援方法攔截")
    void testMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code", is(1002)))
                .andExpect(jsonPath("$.message", is("method not allowed")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("不存在端點攔截")
    void testNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(1003)))
                .andExpect(jsonPath("$.message", is("resource not found")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
