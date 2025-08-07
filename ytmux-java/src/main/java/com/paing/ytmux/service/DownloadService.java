package com.paing.ytmux.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.paing.ytmux.dto.DownloadRequest;
import com.paing.ytmux.model.Track;
import com.paing.ytmux.repository.TrackRepository;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class DownloadService {

    @Autowired
    private TrackRepository trackRepository;

    private String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public Map<String, Object> downloadTrack(DownloadRequest request) throws IOException, JsonSyntaxException {
        String url = request.getUrl();
        String folder = request.getFolder() != null && !request.getFolder().isBlank()
                ? request.getFolder()
                : "C:/Users/Paing Htoo Myat/Downloads/Music";

        File outputDir = new File(folder);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create directory.");
        }

        Gson gson = new Gson();

        // Step 1: Get metadata filename from yt-dlp without downloading
        ProcessBuilder metaPb = new ProcessBuilder(
                "yt-dlp",
                "--skip-download",
                "--print-json",
                url
        );
        metaPb.redirectErrorStream(true);
        Process metaProcess = metaPb.start();
        String metaOutput = new String(metaProcess.getInputStream().readAllBytes()).trim();

        if (!metaOutput.startsWith("{")) {
            throw new JsonSyntaxException("yt-dlp did not return valid JSON:\n" + metaOutput);
        }

        Map<String, Object> metaJson = gson.fromJson(metaOutput, Map.class);
        String tentativeFileName = sanitizeFileName((String) metaJson.get("title"));
        String mp3Path = new File(outputDir, tentativeFileName + ".mp3").getCanonicalPath();

        // Step 2: Check DB first
        Optional<Track> existingTrackOpt = trackRepository.findByFilePath(mp3Path);
        if (existingTrackOpt.isPresent()) {
            Track existingTrack = existingTrackOpt.get();
            return Map.of(
                    "title", existingTrack.getTitle(),
                    "artist", existingTrack.getArtist(),
                    "album", existingTrack.getAlbum(),
                    "duration", existingTrack.getDuration(),
                    "thumbnail", metaJson.get("thumbnail"),
                    "filePath", existingTrack.getFilePath(),
                    "alreadyExists", true
            );
        }

        // Step 3: Check if file exists on disk
        File mp3File = new File(mp3Path);
        if (mp3File.exists()) {
            String title = tentativeFileName;
            String artist = (String) metaJson.getOrDefault("uploader", "");
            String album = "";
            Double durationSec = (Double) metaJson.get("duration");
            int duration = durationSec != null ? durationSec.intValue() : 0;

            try {
                AudioFile audioFile = AudioFileIO.read(mp3File);
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    artist = Optional.ofNullable(tag.getFirst(FieldKey.ARTIST)).filter(s -> !s.isEmpty()).orElse(artist);
                    album = Optional.ofNullable(tag.getFirst(FieldKey.ALBUM)).orElse("");
                    title = Optional.ofNullable(tag.getFirst(FieldKey.TITLE)).filter(s -> !s.isEmpty()).orElse(title);
                }
            } catch (Exception e) {
                System.err.println("Metadata read failed: " + e.getMessage());
            }

            // Save to DB
            trackRepository.save(new Track(title, artist, album, duration, mp3Path));

            return Map.of(
                    "title", title,
                    "artist", artist,
                    "album", album,
                    "duration", duration,
                    "thumbnail", metaJson.get("thumbnail"),
                    "filePath", mp3Path,
                    "alreadyExists", true
            );
        }

        // Step 4: Download track
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
            throw new JsonSyntaxException("yt-dlp did not return valid JSON:\n" + output);
        }

        metaJson = gson.fromJson(output, Map.class);
        tentativeFileName = sanitizeFileName((String) metaJson.get("title"));
        mp3Path = new File(outputDir, tentativeFileName + ".mp3").getCanonicalPath();
        mp3File = new File(mp3Path);

        if (!mp3File.exists()) {
            throw new IOException("MP3 file not found at: " + mp3Path);
        }

        String title = tentativeFileName;
        String artist = (String) metaJson.getOrDefault("uploader", "");
        String album = "";
        Double durationSec = (Double) metaJson.get("duration");
        int duration = durationSec != null ? durationSec.intValue() : 0;
        String thumbnail = (String) metaJson.get("thumbnail");

        try {
            AudioFile audioFile = AudioFileIO.read(mp3File);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                artist = Optional.ofNullable(tag.getFirst(FieldKey.ARTIST)).filter(s -> !s.isEmpty()).orElse(artist);
                album = Optional.ofNullable(tag.getFirst(FieldKey.ALBUM)).orElse("");
                title = Optional.ofNullable(tag.getFirst(FieldKey.TITLE)).filter(s -> !s.isEmpty()).orElse(title);
            }
        } catch (Exception e) {
            System.err.println("Metadata read failed: " + e.getMessage());
        }

        // Save new track
        trackRepository.save(new Track(title, artist, album, duration, mp3Path));

        return Map.of(
                "title", title,
                "artist", artist,
                "album", album,
                "duration", duration,
                "thumbnail", thumbnail,
                "filePath", mp3Path,
                "alreadyExists", false
        );
    }
}
