package com.paing.ytmux.controller;

import com.paing.ytmux.dto.TrackMetadata;
import com.paing.ytmux.service.TrackMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracks")
public class UpdateMetaDataController {

    @Autowired
    private TrackMetadataService metadataService;

    @PutMapping("/edit")
    public ResponseEntity<?> editTrack(@RequestBody TrackMetadata metadata) {
        try {
            String message = metadataService.updateTrackMetadata(metadata);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating metadata: " + e.getMessage());
        }
    }
}
