package com.hamza.microservises.mediaserver;

import com.hamza.microservises.mediaserver.dao.entity.CreatorEntity;
import com.hamza.microservises.mediaserver.dao.entity.VideoEntity;
import com.hamza.microservises.mediaserver.dao.mapper.CreatorMapper;
import com.hamza.microservises.mediaserver.dao.mapper.VideoMapper;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.xproce.lab.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@GrpcService
public class CreatorService extends CreatorServiceGrpc.CreatorServiceImplBase {

    public List<CreatorEntity> listCreators = new ArrayList<>();

    @Autowired
    private CreatorMapper creatorMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoService videoService;


    @Override
    public void getCreator(CreatorIdRequest request, StreamObserver<Creator> responseObserver) {
        CreatorEntity creatorEntity = listCreators.stream()
                .filter(creator -> creator.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Creator not found with id: " + request.getId())
                        .asRuntimeException());

        Creator creator = creatorMapper.toCreatorProto(creatorEntity);
        responseObserver.onNext(creator);
        responseObserver.onCompleted();
    }

    @Override
    public void getCreatorVideos(CreatorIdRequest request, StreamObserver<VideoStream> responseObserver) {
        try {
            System.out.println("Received request for creator ID: " + request.getId());

            // Check if creator exists
            boolean creatorExists = listCreators.stream()
                    .anyMatch(creator -> creator.getId().equals(request.getId()));

            if (!creatorExists) {
                System.out.println("Creator not found with ID: " + request.getId());
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription("Creator not found with id: " + request.getId())
                                .asRuntimeException()
                );
                return;
            }

            // Get all videos for the creator using static list
            List<VideoEntity> creatorVideos = videoService.listVideos.stream()
                    .filter(video -> {
                        if (video == null || video.getCreator() == null) {
                            return false;
                        }
                        return video.getCreator().getId().equals(request.getId());
                    })
                    .collect(Collectors.toList());

            System.out.println("Found videos for creator: " + creatorVideos.size());

            List<Video> protoVideos = new ArrayList<>();
            for (VideoEntity videoEntity : creatorVideos) {
                try {
                    Video protoVideo = videoMapper.toVideoProto(videoEntity);
                    System.out.println("Error mapping video: =>" + protoVideo.getId());
                    protoVideos.add(protoVideo);
                } catch (Exception e) {
                    System.out.println("Error mapping video: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Create VideoStream response
            VideoStream videoStream = VideoStream.newBuilder()
                    .addAllVideos(protoVideos)
                    .build();

            System.out.println("Sending response with " + protoVideos.size() + " videos");

            responseObserver.onNext(videoStream);
            responseObserver.onCompleted();

        } catch (Exception e) {
            System.out.println("Error in getCreatorVideos: " + e.getMessage());
            e.printStackTrace();
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error while retrieving creator videos: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

}