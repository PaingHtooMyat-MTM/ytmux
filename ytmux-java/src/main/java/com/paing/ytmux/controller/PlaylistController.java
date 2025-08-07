package com.paing.ytmux.controller;

import com.paing.ytmux.model.Playlist;
import com.paing.ytmux.model.Track;
import com.paing.ytmux.service.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        return ResponseEntity.ok(playlistService.createPlaylist(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistId}/add/{trackId}")
    public ResponseEntity<Playlist> addTrack(@PathVariable Long playlistId, @PathVariable Long trackId) {
        return ResponseEntity.ok(playlistService.addTrackToPlaylist(playlistId, trackId));
    }

    @PostMapping("/{playlistId}/remove/{trackId}")
    public ResponseEntity<Playlist> removeTrack(@PathVariable Long playlistId, @PathVariable Long trackId) {
        return ResponseEntity.ok(playlistService.removeTrackFromPlaylist(playlistId, trackId));
    }

    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<List<Track>> getTracks(@PathVariable Long playlistId) {
        return ResponseEntity.ok(playlistService.getTracksInPlaylist(playlistId));
    }
}
