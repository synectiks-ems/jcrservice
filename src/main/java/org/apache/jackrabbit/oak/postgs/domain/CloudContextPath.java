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
 * A ColudContextPath.
 */
@Entity
@Table(name = "cloud_context_path")
public class CloudContextPath implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "provider")
    private String provider;
    
    @Column(name = "plugin")
    private String plugin;

    @Size(max = 2000)
    @Column(name = "path", length = 2000)
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlugin() {
        return plugin;
    }

    public CloudContextPath plugin(String plugin) {
        this.plugin = plugin;
        return this;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getPath() {
        return path;
    }

    public CloudContextPath path(String path) {
        this.path = path;
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudContextPath)) {
            return false;
        }
        return id != null && id.equals(((CloudContextPath) o).id);
    }

    @Override
    public int hashCode() {
        return 41;
    }

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "CloudContextPath [id=" + id + ", provider=" + provider + ", plugin=" + plugin + ", path=" + path + "]";
	}

   
}
