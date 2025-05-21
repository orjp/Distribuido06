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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yojha
 */
@Entity
@Table(name = "venta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venta.findAll", query = "SELECT v FROM Venta v"),
    @NamedQuery(name = "Venta.findByCodiVent", query = "SELECT v FROM Venta v WHERE v.codiVent = :codiVent"),
    @NamedQuery(name = "Venta.findByFechVent", query = "SELECT v FROM Venta v WHERE v.fechVent = :fechVent")})
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codiVent")
    private Integer codiVent;
    @Size(max = 45)
    @Column(name = "fechVent")
    private String fechVent;
    @JoinColumn(name = "codiClie", referencedColumnName = "codiClie")
    @ManyToOne(optional = false)
    private Cliente codiClie;

    public Venta() {
    }

    public Venta(Integer codiVent) {
        this.codiVent = codiVent;
    }

    public Integer getCodiVent() {
        return codiVent;
    }

    public void setCodiVent(Integer codiVent) {
        this.codiVent = codiVent;
    }

    public String getFechVent() {
        return fechVent;
    }

    public void setFechVent(String fechVent) {
        this.fechVent = fechVent;
    }

    public Cliente getCodiClie() {
        return codiClie;
    }

    public void setCodiClie(Cliente codiClie) {
        this.codiClie = codiClie;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codiVent != null ? codiVent.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Venta)) {
            return false;
        }
        Venta other = (Venta) object;
        if ((this.codiVent == null && other.codiVent != null) || (this.codiVent != null && !this.codiVent.equals(other.codiVent))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Venta[ codiVent=" + codiVent + " ]";
    }
    
}
