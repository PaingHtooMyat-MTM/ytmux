package com.paing.ytmux.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.paing.ytmux.dto.DownloadRequest;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DownloadController {

    @PostMapping("/download")
    public ResponseEntity<Map<String, Object>> download(@RequestBody DownloadRequest request) {
        String url = request.getUrl();
        String folder = request.getFolder() != null && !request.getFolder().isBlank()
                ? request.getFolder()
                : "C:/Users/Paing Htoo Myat/Downloads/Music";

        try {
            File outputDir = new File(folder);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to create directory."));
            }

            String outputTemplate = outputDir.getCanonicalPath() + "/%(title)s.%(ext)s";

            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "-x", "--audio-format", "mp3",
                    "--embed-metadata",
                    "--embed-thumbnail",
                    "--add-metadata",
                    "-o", outputTemplate,
                    "--print-json",
                    url
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();

            if (!output.startsWith("{")) {
                return ResponseEntity.badRequest().body(Map.of("error", "yt-dlp did not return valid JSON", "output", output));
            }

            Gson gson = new Gson();
            Map<String, Object> json = gson.fromJson(output, Map.class);

            String title = (String) json.get("title");
            String uploader = (String) json.get("uploader");
            String thumbnail = (String) json.get("thumbnail");
            Double durationSec = (Double) json.get("duration");

            String mp3Path = outputDir.getCanonicalPath() + "/" + title + ".mp3";
            File mp3File = new File(mp3Path);
            if (!mp3File.exists()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Downloaded MP3 not found."));
            }

            String artist = uploader;
            String album = "";
            String durationFormatted = formatDuration(durationSec != null ? durationSec.intValue() : 0);

            try {
                AudioFile audioFile = AudioFileIO.read(mp3File);
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    artist = Optional.ofNullable(tag.getFirst(FieldKey.ARTIST)).filter(s -> !s.isEmpty()).orElse(artist);
                    album = Optional.ofNullable(tag.getFirst(FieldKey.ALBUM)).orElse("");
                }
            } catch (Exception e) {
                System.err.println("Metadata read failed: " + e.getMessage());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("title", title);
            result.put("artist", artist);
            result.put("album", album);
            result.put("duration", durationFormatted);
            result.put("thumbnail", thumbnail);
            result.put("filePath", mp3Path);

            return ResponseEntity.ok(result);

        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Exception occurred", "message", e.getMessage()));
        }
    }

    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
