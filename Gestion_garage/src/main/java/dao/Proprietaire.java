package dao;

import javax.persistence.*;

@Entity
@Table(name = "proprietaire")
public class Proprietaire extends Utilisateur {

    public Proprietaire() {}

    public Proprietaire(String nom, String email, String password, Boutique boutique) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.boutique = boutique;
    }
}
