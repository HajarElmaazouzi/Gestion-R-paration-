package dao;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@Entity
@Table(name = "reparateurs")
public class ReparateurDAO extends UserDAO{


	private double pourcentage;
	@ManyToOne
	private BoutiqueDAO boutique ;
	
	
	@Override
	public String toString() {
	    return "Reparateur{" +
	            "id=" + getId() +
	            ", username='" + getUsername() + '\'' +
	            ", pourcentage=" + pourcentage +
	            // juste l'id ou le nom de la boutique, pas l'objet complet
	            ", boutique=" + (boutique != null ? boutique.getId() : "null") +
	            '}';
	}



}
