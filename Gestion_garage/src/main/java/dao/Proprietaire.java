package dao;

import javax.persistence.*;

@Entity
public class Proprietaire extends Utilisateur {

    @OneToOne
    private Boutique boutique;

    public Boutique getBoutique() { return boutique; }
    public void setBoutique(Boutique boutique) { this.boutique = boutique; }
}
