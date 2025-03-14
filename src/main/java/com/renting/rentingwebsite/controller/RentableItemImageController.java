package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.RentableItemDTO;
import com.renting.rentingwebsite.repository.RentableItemImageRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import jakarta.annotation.security.PermitAll;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/image")
public class RentableItemImageController {

    private final Logger logger = LoggerFactory.getLogger(RentableItemImageController.class);

    private final RentableItemRepository rentableItemRepository;

    private final RentableItemImageRepository rentableItemImageRepository;

    private final String uploadDir = "images";

    public RentableItemImageController(RentableItemRepository rentableItemRepository, RentableItemImageRepository rentableItemImageRepository) {
        this.rentableItemRepository = rentableItemRepository;
        this.rentableItemImageRepository = rentableItemImageRepository;
    }

    @GetMapping("/{rentableId}/{showId}")
    @PermitAll
    public ResponseEntity<Resource> getRentable(@PathVariable long rentableId, @PathVariable long showId) throws IOException {

        var rentable =  rentableItemRepository.findById(rentableId).orElseThrow(
                () -> new BadRequestException("No rentable item exists with id: " + rentableId)
        );

        var image = rentableItemImageRepository.findByShowIndexAndRentableItem(showId, rentable).orElseThrow(
                () -> new BadRequestException("No rentable item image exists with showID: " + showId)
        );

        Path imagePath = Paths.get(uploadDir).resolve(image.getImageName()).normalize();
        Resource resource = new UrlResource(imagePath.toUri());

        logger.warn("imageName: {}", image.getImageName());
        logger.warn("path: {}", imagePath.toAbsolutePath());
        logger.warn("resource: {}", resource.getURI());

        logger.warn("Resource exists: {}", resource.exists());
        logger.warn("Resource isReadable: {}", resource.isReadable());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var contentType = determineContentType(image.getImageName());


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getImageName() + "\"")
                .body(resource);
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // Default type
    }
}
