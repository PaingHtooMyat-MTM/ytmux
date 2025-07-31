package com.paing.ytmux.controller;

import com.paing.ytmux.dto.TrackMetadata;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/tracks")
public class UpdateMetaDataController {

    @PutMapping("/edit")
    public ResponseEntity<?> editTrack(@RequestBody TrackMetadata metadata) {
        if (metadata.getPath() == null) {
            return ResponseEntity.badRequest().body("Missing file path");
        }

        try {
            File audioFile = new File(metadata.getPath());
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();

            if (tag != null) {
                if (metadata.getTitle() != null) tag.setField(FieldKey.TITLE, metadata.getTitle());
                if (metadata.getArtist() != null) tag.setField(FieldKey.ARTIST, metadata.getArtist());
                if (metadata.getAlbum() != null) tag.setField(FieldKey.ALBUM, metadata.getAlbum());
                f.commit();
            }

            return ResponseEntity.ok("Metadata updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating metadata: " + e.getMessage());
        }
    }
}
