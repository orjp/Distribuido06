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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Naomi Alejandra Vega
 */
@Entity
@Table(name = "kardex")
@NamedQueries({
    @NamedQuery(name = "Kardex.findAll", query = "SELECT k FROM Kardex k"),
    @NamedQuery(name = "Kardex.findByCodiKard", query = "SELECT k FROM Kardex k WHERE k.codiKard = :codiKard"),
    @NamedQuery(name = "Kardex.findByCantProd", query = "SELECT k FROM Kardex k WHERE k.cantProd = :cantProd"),
    @NamedQuery(name = "Kardex.findBySaldProd", query = "SELECT k FROM Kardex k WHERE k.saldProd = :saldProd"),
    @NamedQuery(name = "Kardex.findByMoviKard", query = "SELECT k FROM Kardex k WHERE k.moviKard = :moviKard")})
public class Kardex implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "codiKard")
    private Integer codiKard;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantProd")
    private int cantProd;
    @Basic(optional = false)
    @NotNull
    @Column(name = "saldProd")
    private int saldProd;
    @Basic(optional = false)
    @NotNull
    @Column(name = "moviKard")
    private int moviKard;
    @JoinColumn(name = "codiProd", referencedColumnName = "codiProd")
    @ManyToOne(optional = false)
    private Producto codiProd;

    public Kardex() {
    }

    public Kardex(Integer codiKard) {
        this.codiKard = codiKard;
    }

    public Kardex(Integer codiKard, int cantProd, int saldProd, int moviKard) {
        this.codiKard = codiKard;
        this.cantProd = cantProd;
        this.saldProd = saldProd;
        this.moviKard = moviKard;
    }

    public Integer getCodiKard() {
        return codiKard;
    }

    public void setCodiKard(Integer codiKard) {
        this.codiKard = codiKard;
    }

    public int getCantProd() {
        return cantProd;
    }

    public void setCantProd(int cantProd) {
        this.cantProd = cantProd;
    }

    public int getSaldProd() {
        return saldProd;
    }

    public void setSaldProd(int saldProd) {
        this.saldProd = saldProd;
    }

    public int getMoviKard() {
        return moviKard;
    }

    public void setMoviKard(int moviKard) {
        this.moviKard = moviKard;
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
        hash += (codiKard != null ? codiKard.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kardex)) {
            return false;
        }
        Kardex other = (Kardex) object;
        if ((this.codiKard == null && other.codiKard != null) || (this.codiKard != null && !this.codiKard.equals(other.codiKard))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Kardex[ codiKard=" + codiKard + " ]";
    }
    
}
