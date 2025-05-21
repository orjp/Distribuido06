/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yojha
 */
@Entity
@Table(name = "detalle")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalle.findAll", query = "SELECT d FROM Detalle d"),
    @NamedQuery(name = "Detalle.findByCodiDeta", query = "SELECT d FROM Detalle d WHERE d.codiDeta = :codiDeta"),
    @NamedQuery(name = "Detalle.findByCantDeta", query = "SELECT d FROM Detalle d WHERE d.cantDeta = :cantDeta"),
    @NamedQuery(name = "Detalle.findByPrecProd", query = "SELECT d FROM Detalle d WHERE d.precProd = :precProd"),
    @NamedQuery(name = "Detalle.findBySbttDeta", query = "SELECT d FROM Detalle d WHERE d.sbttDeta = :sbttDeta")})
public class Detalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codiDeta")
    private Integer codiDeta;
    @Column(name = "cantDeta")
    private Integer cantDeta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precProd")
    private Double precProd;
    @Column(name = "sbttDeta")
    private Double sbttDeta;
    @JoinColumn(name = "codiVent", referencedColumnName = "codiVent")
    @ManyToOne(optional = false)
    private Venta codiVent;
    @JoinColumn(name = "codiProd", referencedColumnName = "codiProd")
    @ManyToOne(optional = false)
    private Producto codiProd;

    public Detalle() {
    }

    public Detalle(Integer codiDeta) {
        this.codiDeta = codiDeta;
    }

    public Integer getCodiDeta() {
        return codiDeta;
    }

    public void setCodiDeta(Integer codiDeta) {
        this.codiDeta = codiDeta;
    }

    public Integer getCantDeta() {
        return cantDeta;
    }

    public void setCantDeta(Integer cantDeta) {
        this.cantDeta = cantDeta;
    }

    public Double getPrecProd() {
        return precProd;
    }

    public void setPrecProd(Double precProd) {
        this.precProd = precProd;
    }

    public Double getSbttDeta() {
        return sbttDeta;
    }

    public void setSbttDeta(Double sbttDeta) {
        this.sbttDeta = sbttDeta;
    }

    public Venta getCodiVent() {
        return codiVent;
    }

    public void setCodiVent(Venta codiVent) {
        this.codiVent = codiVent;
    }

    public Producto getCodiProd() {
        return codiProd;
    }

    public void setCodiProd(Producto codiProd) {
        this.codiProd = codiProd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codiDeta != null ? codiDeta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalle)) {
            return false;
        }
        Detalle other = (Detalle) object;
        if ((this.codiDeta == null && other.codiDeta != null) || (this.codiDeta != null && !this.codiDeta.equals(other.codiDeta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Detalle[ codiDeta=" + codiDeta + " ]";
    }
    
}
