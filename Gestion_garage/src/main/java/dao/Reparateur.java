package dao;

import javax.persistence.*;

@Entity
public class Reparateur extends Utilisateur {

    private String telephone;
    private String specialite;

    @ManyToOne
    private Boutique boutique;

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public Boutique getBoutique() { return boutique; }
    public void setBoutique(Boutique boutique) { this.boutique = boutique; }
}
