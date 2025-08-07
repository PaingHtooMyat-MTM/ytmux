package com.paing.ytmux.repository;
import java.util.Optional;

import com.paing.ytmux.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByFilePath(String filePath);
    boolean existsByFilePath(String filePath);
}
