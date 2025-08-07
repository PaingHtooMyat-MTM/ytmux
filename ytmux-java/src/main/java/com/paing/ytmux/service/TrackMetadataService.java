package com.paing.ytmux.service;

import com.paing.ytmux.dto.TrackMetadata;
import com.paing.ytmux.model.Track;
import com.paing.ytmux.repository.TrackRepository;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class TrackMetadataService {

    @Autowired
    private TrackRepository trackRepository;

    public String updateTrackMetadata(TrackMetadata metadata) throws Exception {
        if (metadata.getPath() == null || metadata.getPath().isBlank()) {
            throw new IllegalArgumentException("Missing file path");
        }

        File audioFile = new File(metadata.getPath());
        if (!audioFile.exists() || !audioFile.isFile()) {
            throw new IllegalArgumentException("Audio file not found at path: " + metadata.getPath());
        }

        // Update file tags
        AudioFile f = AudioFileIO.read(audioFile);
        Tag tag = f.getTag();
        if (tag != null) {
            if (metadata.getTitle() != null) tag.setField(FieldKey.TITLE, metadata.getTitle());
            if (metadata.getArtist() != null) tag.setField(FieldKey.ARTIST, metadata.getArtist());
            if (metadata.getAlbum() != null) tag.setField(FieldKey.ALBUM, metadata.getAlbum());
            f.commit();
        }

        // Update database entry if it exists
        Optional<Track> dbTrack = trackRepository.findByFilePath(metadata.getPath());
        if (dbTrack.isPresent()) {
            Track track = dbTrack.get();
            if (metadata.getTitle() != null) track.setTitle(metadata.getTitle());
            if (metadata.getArtist() != null) track.setArtist(metadata.getArtist());
            if (metadata.getAlbum() != null) track.setAlbum(metadata.getAlbum());
            trackRepository.save(track);
        }

        return "Metadata updated successfully";
    }
}
