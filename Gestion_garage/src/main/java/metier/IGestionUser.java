package metier;

import java.util.List;

import dao.ReparateurDAO;
import dao.UserDAO;

public interface IGestionUser {
	 
	void CreerReparateur(ReparateurDAO reparateur);
	void ModifierReparateur(int id, ReparateurDAO reparateur);
	void SupprimerReparateur(int idReparateur);
	List<ReparateurDAO> ListerReparateurs();
	
	UserDAO SeConnecter(String username, String password);
	

}
