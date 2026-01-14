package dao;

import javax.persistence.*;
import lombok.*;
import java.util.Date;



@Entity
@Table(name = "appareils")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appareil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "imei", unique = true, nullable = false, length = 15)
    private String imei;
    
    @Column(name = "marque", nullable = false, length = 50)
    private String marque;
    
    @Column(name = "modele", nullable = false, length = 50)
    private String modele;
    
  
    @Column(name = "type_appareil", length = 30)
    private String typeAppareil;
    
    
    @Column(name = "etat", length = 20)
    @Enumerated(EnumType.STRING)  // Stockage de l'état sous forme de chaîne
    private EtatAppareil etat = EtatAppareil.DISPONIBLE;
    
    @Column(name = "date_ajout")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAjout = new Date();
}
