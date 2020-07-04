package org.apache.jackrabbit.oak.postgs.domain;

import java.io.Serializable;

public class CloudInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private String provider;
    private String plugin;
    private String contextPath;

    private String accessKey;
    private String secrateKey;
    private String bucket;
    private String endPoint;
    private String permissionMode;
    private String permissionModeValue;
    
    private String filename;
    private String localFile;
    private String localFilePath;
    private String cloudFilePath;
    private String clientType;
    private Long clientId;
    private String cdn;
    private String status;
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getPlugin() {
		return plugin;
	}
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecrateKey() {
		return secrateKey;
	}
	public void setSecrateKey(String secrateKey) {
		this.secrateKey = secrateKey;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	public String getPermissionMode() {
		return permissionMode;
	}
	public void setPermissionMode(String permissionMode) {
		this.permissionMode = permissionMode;
	}
	public String getPermissionModeValue() {
		return permissionModeValue;
	}
	public void setPermissionModeValue(String permissionModeValue) {
		this.permissionModeValue = permissionModeValue;
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
		return "CloudInfo [provider=" + provider + ", plugin=" + plugin + ", contextPath=" + contextPath
				+ ", accessKey=" + accessKey + ", secrateKey=" + secrateKey + ", bucket=" + bucket + ", endPoint="
				+ endPoint + ", permissionMode=" + permissionMode + ", permissionModeValue=" + permissionModeValue
				+ ", filename=" + filename + ", localFilePath=" + localFilePath + ", cloudFilePath=" + cloudFilePath
				+ ", clientType=" + clientType + ", clientId=" + clientId + ", cdn=" + cdn + ", status=" + status + "]";
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getLocalFile() {
		return localFile;
	}
	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

}
