package com.bigname.pim.api.persistence.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by manu on 8/18/18.
 */
public interface BaseDAO<T> {
    Page<T> findAllByActiveIn(String active[], Pageable pageable);

    Page<T> findByIdNotInAndActiveIn(String[] ids, String[] active, Pageable pageable);
    Page<T> findByExternalIdNotInAndActiveIn(String[] ids, String[] active, Pageable pageable);

    Page<T> findAllByIdInAndActiveIn(String[] ids, String[] active, Pageable pageable);
    Page<T> findAllByExternalIdInAndActiveIn(String[] ids, String[] active, Pageable pageable);

    Optional<T> findByIdAndActiveIn(String id, String[] active);
    Optional<T> findByExternalIdAndActiveIn(String id, String[] active);
    long countByIdNotNull();

}
