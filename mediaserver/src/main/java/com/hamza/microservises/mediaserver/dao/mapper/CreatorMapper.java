package com.hamza.microservises.mediaserver.dao.mapper;

import com.hamza.microservises.mediaserver.dao.entity.CreatorEntity;
import org.springframework.stereotype.Component;
import org.xproce.lab.Creator;

@Component
public class CreatorMapper {
    public CreatorEntity toCreatorEntity(Creator creator) {
        return CreatorEntity.builder()
                .id(creator.getId())
                .name(creator.getName())
                .email(creator.getEmail())
                .build();
    }

    public Creator toCreatorProto(CreatorEntity entity) {
        return Creator.newBuilder()
                .setId(entity.getId())
                .setName(entity.getName())
                .setEmail(entity.getEmail())
                .build();
    }
}