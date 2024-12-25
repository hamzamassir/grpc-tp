package com.hamza.microservises.mediaserver.dao.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoEntity {
    private String id;
    private String title;
    private String description;
    private String url;
    private int durationSeconds;

    private CreatorEntity creator;
}