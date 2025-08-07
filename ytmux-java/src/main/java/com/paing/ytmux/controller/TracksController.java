package com.paing.ytmux.controller;

import com.paing.ytmux.model.Track;
import com.paing.ytmux.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class TracksController {

    @Autowired
    private TrackService trackService;

    @GetMapping("/api/tracks")
    public ResponseEntity<?> listTracks(@RequestParam(required = false) String folder) {
        String musicFolder = (folder != null && !folder.isBlank())
                ? folder
                : "C:/Users/Paing Htoo Myat/Downloads/Music";

        try {
            List<Track> result = trackService.scanAndSyncTracks(musicFolder);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
