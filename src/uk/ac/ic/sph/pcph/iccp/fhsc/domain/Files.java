/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ic.sph.pcph.iccp.fhsc.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lkc-dev
 */
@Entity
@Table(name = "Files", catalog = "fhsc_db", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Files.findAll", query = "SELECT f FROM Files f"),
    @NamedQuery(name = "Files.findByFileId", query = "SELECT f FROM Files f WHERE f.fileId = :fileId"),
    @NamedQuery(name = "Files.findByFileName", query = "SELECT f FROM Files f WHERE f.fileName = :fileName"),
    @NamedQuery(name = "Files.findByFileSize", query = "SELECT f FROM Files f WHERE f.fileSize = :fileSize"),
    @NamedQuery(name = "Files.findByLocation", query = "SELECT f FROM Files f WHERE f.location = :location"),
    @NamedQuery(name = "Files.findByLocationAndName", query = "SELECT f FROM Files f WHERE f.location = :location and f.fileName=:name"),
    @NamedQuery(name = "Files.findByUploadDate", query = "SELECT f FROM Files f WHERE f.uploadDate = :uploadDate"),
    @NamedQuery(name = "Files.findByFileType", query = "SELECT f FROM Files f WHERE f.fileType = :fileType"),
    @NamedQuery(name = "Files.findByUserID", query = "SELECT f FROM Files f WHERE f.userId = :userId"),
    @NamedQuery(name = "Files.findByComment", query = "SELECT f FROM Files f WHERE f.comment = :comment")})
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "file_id")
    private Integer fileId;
    @Basic(optional = false)
    @Column(name = "file_name")
    private String fileName;
    @Basic(optional = false)
    @Column(name = "file_size")
    private int fileSize;
    @Basic(optional = false)
    @Column(name = "location")
    private String location;
    @Basic(optional = false)
    @Column(name = "upload_date")
    @Temporal(TemporalType.DATE)
    private Date uploadDate;
    @Basic(optional = false)
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "comment")
    private String comment;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userId;

    public Files() {
    }

    public Files(Integer fileId) {
        this.fileId = fileId;
    }

    public Files(Integer fileId, String fileName, int fileSize, String location, Date uploadDate, String fileType) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.location = location;
        this.uploadDate = uploadDate;
        this.fileType = fileType;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fileId != null ? fileId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Files)) {
            return false;
        }
        Files other = (Files) object;
        if ((this.fileId == null && other.fileId != null) || (this.fileId != null && !this.fileId.equals(other.fileId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.ic.sph.pcph.iccp.fhsc.domain.Files[ fileId=" + fileId + " ]";
    }
    
}
