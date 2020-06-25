package org.apache.jackrabbit.oak.postgs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Documents.
 */
@Entity
@Table(name = "documents")
public class Documents implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "file_name")
    private String fileName;
    
    @Size(max = 500)
    @Column(name = "local_file_path", length = 500)
    private String localFilePath;

    @Size(max = 2000)
    @Column(name = "cloud_file_path", length = 2000)
    private String cloudFilePath;

    @Size(max = 20)
    @Column(name = "client_type", length = 20)
    private String clientType;

    @Column(name = "client_id")
    private Long clientId;
    
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private CloudContextPath CloudContextPath;
    
    @Column(name = "cdn")
    private String cdn;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdn == null) ? 0 : cdn.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
		result = prime * result + ((cloudFilePath == null) ? 0 : cloudFilePath.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localFilePath == null) ? 0 : localFilePath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Documents other = (Documents) obj;
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public String getCloudFilePath() {
		return cloudFilePath;
	}

	public void setCloudFilePath(String cloudFilePath) {
		this.cloudFilePath = cloudFilePath;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public CloudContextPath getCloudContextPath() {
		return CloudContextPath;
	}

	public void setCloudContextPath(CloudContextPath cloudContextPath) {
		CloudContextPath = cloudContextPath;
	}

	public String getCdn() {
		return cdn;
	}

	public void setCdn(String cdn) {
		this.cdn = cdn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Documents [id=" + id + ", fileName=" + fileName + ", localFilePath=" + localFilePath
				+ ", cloudFilePath=" + cloudFilePath + ", clientType=" + clientType + ", clientId=" + clientId
				+ ", CloudContextPath=" + CloudContextPath + ", cdn=" + cdn + ", status=" + status + "]";
	}
    
    

   
}
