package org.apache.jackrabbit.oak.postgs.repository;

import org.apache.jackrabbit.oak.postgs.domain.CloudContextPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ContextPath entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CloudContextPathRepository extends JpaRepository<CloudContextPath, Long> {

}
