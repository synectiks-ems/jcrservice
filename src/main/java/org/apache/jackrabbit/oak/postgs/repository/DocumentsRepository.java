package org.apache.jackrabbit.oak.postgs.repository;

import org.apache.jackrabbit.oak.postgs.domain.Documents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Documents entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Long> {

}
