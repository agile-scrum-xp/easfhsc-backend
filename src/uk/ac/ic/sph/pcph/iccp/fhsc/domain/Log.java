/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ic.sph.pcph.iccp.fhsc.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
//

/**
 *
 * @author lkc-dev
 */
@Entity
@Table(name = "Log", catalog = "fhsc_db", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findByLogId", query = "SELECT l FROM Log l WHERE l.logId = :logId"),
    @NamedQuery(name = "Log.findByLogDate", query = "SELECT l FROM Log l WHERE l.logDate = :logDate"),
    @NamedQuery(name = "Log.findByActivity", query = "SELECT l FROM Log l WHERE l.activity = :activity")})
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "log_id")
    private Integer logId;
    @Basic(optional = false)
    @Column(name = "log_date")
    private String logDate;
    @Basic(optional = false)
    @Column(name = "activity")
    private String activity;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userId;

    public Log() {
    }

    public Log(Integer logId) {
        this.logId = logId;
    }

    public Log(Integer logId, String logDate, String activity) {
        this.logId = logId;
        this.logDate = logDate;
        this.activity = activity;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
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
        hash += (logId != null ? logId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.logId == null && other.logId != null) || (this.logId != null && !this.logId.equals(other.logId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.ic.sph.pcph.iccp.fhsc.domain.Log[ logId=" + logId + " ]";
    }
    
}
