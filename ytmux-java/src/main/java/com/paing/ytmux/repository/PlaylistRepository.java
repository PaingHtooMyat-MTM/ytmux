package com.paing.ytmux.repository;

import com.paing.ytmux.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    boolean existsByName(String name);
}
