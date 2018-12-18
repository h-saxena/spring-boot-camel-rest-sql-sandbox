package io.fabric8.quickstarts.camel.db.model.crmp;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.fabric8.quickstarts.camel.db.model.BooleanToStringConverter;

/**
 * The persistent class for the CRM_PROPERTIES database table.
 *
 * 
 */
@Entity
@Table(name = "CRM_VIEW_QUERIES")
public class CrmViewQuery implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The id. */
  @Id
  @SequenceGenerator(name = "CRM_VIEW_QUERIES_ID_GENERATOR", sequenceName = "CRM_VIEW_QUERIES_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRM_VIEW_QUERIES_ID_GENERATOR")
  @Column(name = "ID", unique = true, nullable = false, precision = 22)
  private long id;

  /** The prop name. */
  @Column(name = "NAME")
  private String name;

  /** The prop value. */
  @Column(name = "TEXT")
  @Lob
  private String text;

  @Column(name = "SOR")
  private String sor;

  @Column(name = "DESCRIPTION")
  private String desc;

  @Convert(converter = BooleanToStringConverter.class)
  @Column(name = "ENABLED")
  private boolean enabled = true;  

  @Column(name = "CREATED")
  @Type(type = "timestamp")
  private Timestamp created;

  @Column(name = "CREATED_BY", length = 100)
  private String createdBy;

  @Column(name = "LAST_UPDATED")
  @Type(type = "timestamp")
  private Timestamp lastUpdated;

  @Column(name = "LAST_UPDATED_BY", length = 100)
  private String lastUpdatedBy;
  
  @Transient
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String checksum;


  /* Default Constructor */
  public CrmViewQuery() {
    super();
  }

  public CrmViewQuery(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public CrmViewQuery(Long id, String name, String sor) {
    this.id = id;
    this.name = name;
    this.sor = sor;
  }

  public CrmViewQuery(Long id, String name, String sor, String text) {

    this.id = id;
    this.name = name;
    this.sor = sor;
    this.text = text;
  }

  public CrmViewQuery(String name, String sor, String text, Boolean setText) {

    this.name = name;
    this.sor = sor;
    if (setText) {
      this.text = text;
    }
    try {
      byte[] hashBytes = MessageDigest.getInstance("SHA-256")
        .digest(text.getBytes(StandardCharsets.UTF_8));
      this.checksum = DatatypeConverter.printHexBinary(hashBytes);
    } catch (Exception ex) {
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getSor() {
    return sor;
  }

  public void setSor(String sor) {
    this.sor = sor;
  }

public String getDesc() {
	return desc;
}

public void setDesc(String desc) {
	this.desc = desc;
}

public boolean isEnabled() {
	return enabled;
}

public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}

public Timestamp getCreated() {
	return created;
}

public void setCreated(Timestamp created) {
	this.created = created;
}

public String getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}

public Timestamp getLastUpdated() {
	return lastUpdated;
}

public void setLastUpdated(Timestamp lastUpdated) {
	this.lastUpdated = lastUpdated;
}

public String getLastUpdatedBy() {
	return lastUpdatedBy;
}

public void setLastUpdatedBy(String lastUpdatedBy) {
	this.lastUpdatedBy = lastUpdatedBy;
}

public String getChecksum() {

    try {
      if (checksum == null && this.text != null && !this.text.isEmpty()) {
        byte[] hashBytes = MessageDigest.getInstance("SHA-256")
          .digest(this.text.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(hashBytes);
      } else {
        return checksum;
      }

    } catch (Exception ex) {
      return null;
    }

}

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

}