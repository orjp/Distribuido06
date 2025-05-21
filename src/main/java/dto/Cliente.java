/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * @author yojha
 */
@Entity
@Table(name = "cliente")
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByCodiClie", query = "SELECT c FROM Cliente c WHERE c.codiClie = :codiClie"),
    @NamedQuery(name = "Cliente.findByNombClie", query = "SELECT c FROM Cliente c WHERE c.nombClie = :nombClie")})
public class Cliente implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codiClie")
    private Collection<Venta> ventaCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "codiClie")
    private Integer codiClie;
    @Size(max = 45)
    @Column(name = "nombClie")
    private String nombClie;

    public Cliente() {
    }

    public Cliente(Integer codiClie) {
        this.codiClie = codiClie;
    }

    public Integer getCodiClie() {
        return codiClie;
    }

    public void setCodiClie(Integer codiClie) {
        this.codiClie = codiClie;
    }

    public String getNombClie() {
        return nombClie;
    }

    public void setNombClie(String nombClie) {
        this.nombClie = nombClie;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codiClie != null ? codiClie.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.codiClie == null && other.codiClie != null) || (this.codiClie != null && !this.codiClie.equals(other.codiClie))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Cliente[ codiClie=" + codiClie + " ]";
    }

    @XmlTransient
    public Collection<Venta> getVentaCollection() {
        return ventaCollection;
    }

    public void setVentaCollection(Collection<Venta> ventaCollection) {
        this.ventaCollection = ventaCollection;
    }
    
}
