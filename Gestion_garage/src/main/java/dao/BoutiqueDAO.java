package dao;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "Boutique")
public class BoutiqueDAO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id ;
    private String Nom;
    private String Adresse;
	@OneToMany(mappedBy = "boutique" , fetch = javax.persistence.FetchType.LAZY , cascade = CascadeType.REMOVE, orphanRemoval = true) 
	List<ReparateurDAO> reparateurs ;
	
	
	
	@Override
	public String toString() {
	    return "Boutique{" +
	            "id=" + id +
	            ", nom='" + Nom + '\'' +
	            // ne pas afficher la liste des réparateurs
	            '}';
	}


}
