package com.hamza.microservises.mediaserver.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatorEntity {

    private String id;
    private String name;
    private String email;
}