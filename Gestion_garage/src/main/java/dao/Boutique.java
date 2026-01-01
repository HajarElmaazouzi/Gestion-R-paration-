package dao;

import javax.persistence.*;

@Entity
@Table(name = "boutique")
public class Boutique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String adresse;

    public Boutique() {}

    public Boutique(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
}
