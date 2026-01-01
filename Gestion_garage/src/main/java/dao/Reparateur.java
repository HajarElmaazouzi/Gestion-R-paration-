package dao;

import javax.persistence.*;

@Entity
@Table(name = "reparateur")
public class Reparateur extends Utilisateur {

    private String telephone;
    private String specialite;

    public Reparateur() {}

    public Reparateur(String nom, String email, String password,
                      String telephone, String specialite, Boutique boutique) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
        this.specialite = specialite;
        this.boutique = boutique;
    }

    public String getTelephone() { return telephone; }
    public String getSpecialite() { return specialite; }
}
