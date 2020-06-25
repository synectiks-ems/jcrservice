package org.apache.jackrabbit.oak.postgs.repository;

import org.apache.jackrabbit.oak.postgs.domain.CloudProviderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CloudProviderConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CloudProviderConfigRepository extends JpaRepository<CloudProviderConfig, Long> {

}
