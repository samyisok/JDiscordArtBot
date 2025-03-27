package ru.sarahbot.sarah.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sarahbot.sarah.file.dto.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    
}
