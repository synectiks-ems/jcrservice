package org.apache.jackrabbit.oak.postgs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A CloudProviderConfig.
 */
@Entity
@Table(name = "cloud_provider_config")
public class CloudProviderConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "provider")
    private String provider;

    @Size(max = 2000)
    @Column(name = "access_key", length = 2000)
    private String accessKey;

    @Size(max = 2000)
    @Column(name = "secrate_key", length = 2000)
    private String secrateKey;

    @Size(max = 2000)
    @Column(name = "bucket", length = 2000)
    private String bucket;

    @Size(max = 2000)
    @Column(name = "end_point", length = 2000)
    private String endPoint;

    @Column(name = "permission_mode")
    private String permissionMode;

    @Column(name = "permission_mode_value")
    private String permissionModeValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public CloudProviderConfig provider(String provider) {
        this.provider = provider;
        return this;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public CloudProviderConfig accessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecrateKey() {
        return secrateKey;
    }

    public CloudProviderConfig secrateKey(String secrateKey) {
        this.secrateKey = secrateKey;
        return this;
    }

    public void setSecrateKey(String secrateKey) {
        this.secrateKey = secrateKey;
    }

    public String getBucket() {
        return bucket;
    }

    public CloudProviderConfig bucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public CloudProviderConfig endPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getPermissionMode() {
        return permissionMode;
    }

    public CloudProviderConfig permissionMode(String permissionMode) {
        this.permissionMode = permissionMode;
        return this;
    }

    public void setPermissionMode(String permissionMode) {
        this.permissionMode = permissionMode;
    }

    public String getPermissionModeValue() {
        return permissionModeValue;
    }

    public CloudProviderConfig permissionModeValue(String permissionModeValue) {
        this.permissionModeValue = permissionModeValue;
        return this;
    }

    public void setPermissionModeValue(String permissionModeValue) {
        this.permissionModeValue = permissionModeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudProviderConfig)) {
            return false;
        }
        return id != null && id.equals(((CloudProviderConfig) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CloudProviderConfig{" +
            "id=" + getId() +
            ", provider='" + getProvider() + "'" +
            ", accessKey='" + getAccessKey() + "'" +
            ", secrateKey='" + getSecrateKey() + "'" +
            ", bucket='" + getBucket() + "'" +
            ", endPoint='" + getEndPoint() + "'" +
            ", permissionMode='" + getPermissionMode() + "'" +
            ", permissionModeValue='" + getPermissionModeValue() + "'" +
            "}";
    }
}
