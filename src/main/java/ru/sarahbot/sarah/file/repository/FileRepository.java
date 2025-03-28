package ru.sarahbot.sarah.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sarahbot.sarah.file.dto.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query(value = "SELECT * FROM file_entity ORDER BY RAND() LIMIT 1", nativeQuery = true)
    FileEntity findRandomFileEntity();
}
