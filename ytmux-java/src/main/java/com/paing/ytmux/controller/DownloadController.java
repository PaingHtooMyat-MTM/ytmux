package com.paing.ytmux.controller;

import com.google.gson.JsonSyntaxException;
import com.paing.ytmux.dto.DownloadRequest;
import com.paing.ytmux.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestBody DownloadRequest request) {
        try {
            Map<String, Object> result = downloadService.downloadTrack(request);
            return ResponseEntity.ok(result);
        } catch (JsonSyntaxException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "yt-dlp did not return valid JSON", "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "I/O error occurred", "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error", "message", e.getMessage()));
        }
    }
}
