package com.paing.ytmux.service;

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
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    public List<Track> scanAndSyncTracks(String folder) throws IOException {
        File dir = new File(folder);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Invalid music folder path");
        }

        File[] files = dir.listFiles((f) -> f.isFile() && f.getName().toLowerCase().matches(".*\\.(mp3|wav|flac|m4a)$"));
        if (files == null) return Collections.emptyList();

        List<Track> result = new ArrayList<>();
        for (File file : files) {
            try {
                String normalizedPath = file.getCanonicalPath();

                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();

                String title = (tag != null && !tag.getFirst(FieldKey.TITLE).isEmpty())
                        ? tag.getFirst(FieldKey.TITLE)
                        : file.getName();
                String artist = (tag != null) ? tag.getFirst(FieldKey.ARTIST) : "";
                String album = (tag != null) ? tag.getFirst(FieldKey.ALBUM) : "";
                int duration = audioFile.getAudioHeader().getTrackLength();

                Optional<Track> existingTrack = trackRepository.findByFilePath(normalizedPath);

                if (existingTrack.isPresent()) {
                    result.add(existingTrack.get());
                } else {
                    Track newTrack = new Track(title, artist, album, duration, normalizedPath);
                    Track savedTrack = trackRepository.save(newTrack);
                    result.add(savedTrack);
                }
            } catch (Exception e) {
                System.err.println("Failed to read metadata for " + file.getName() + ": " + e.getMessage());
            }
        }

        return result;
    }
}
