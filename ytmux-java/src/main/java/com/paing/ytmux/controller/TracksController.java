package com.paing.ytmux.controller;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
public class TracksController {

    @GetMapping("/api/tracks")
    public ResponseEntity<?> listTracks(@RequestParam(required = false) String folder) throws IOException {
        String musicFolder = (folder != null && !folder.isBlank())
                ? folder
                : "C:/Users/Paing Htoo Myat/Downloads/Music";

        File dir = new File(musicFolder);
        if (!dir.exists() || !dir.isDirectory()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid music folder path"));
        }

        File[] files = dir.listFiles((f) -> f.isFile() && f.getName().toLowerCase().matches(".*\\.(mp3|wav|flac|m4a)$"));
        if (files == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Map<String, Object>> songs = new ArrayList<>();
        for (File file : files) {
            try {
                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();

                String title = (tag != null && !tag.getFirst(FieldKey.TITLE).isEmpty())
                        ? tag.getFirst(FieldKey.TITLE)
                        : file.getName();

                String artist = (tag != null) ? tag.getFirst(FieldKey.ARTIST) : "";
                String album = (tag != null) ? tag.getFirst(FieldKey.ALBUM) : "";
                int duration = audioFile.getAudioHeader().getTrackLength();
                String durationFormatted = formatDuration(duration);

                Map<String, Object> song = new HashMap<>();
                song.put("title", title);
                song.put("artist", artist);
                song.put("album", album);
                song.put("duration", durationFormatted);
                song.put("filePath", file.getCanonicalPath());

                songs.add(song);
            } catch (Exception e) {
                System.err.println("Failed to read metadata for " + file.getName() + ": " + e.getMessage());
                Map<String, Object> song = new HashMap<>();
                song.put("title", file.getName());
                song.put("artist", "");
                song.put("album", "");
                song.put("duration", "");
//                song.put("filePath", file.getAbsolutePath());

                song.put("filePath", file.getCanonicalPath());
                song.put("playUrl", "/api/play?path=" + file.getCanonicalPath().replace("\\", "/"));
                songs.add(song);
            }
        }

        return ResponseEntity.ok(songs);
    }

    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
