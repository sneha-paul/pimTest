package com.bigname.pim.api.persistence.dao;

import java.util.Optional;

/**
 * Created by manu on 8/31/18.
 */
public interface BaseAssociationDAO<T> {
    Optional<T> findTopBySequenceNumOrderBySubSequenceNumDesc(int sequenceNum);
}
