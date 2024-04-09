package gestionUser;

import java.sql.Timestamp;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role; // "lecteur" ou "admin"
	private int active;
	private Timestamp date_created;
	public Timestamp getDate_created() {
		return date_created;
	}

	public void setDate_created(Timestamp date_created) {
		this.date_created = date_created;
	}

	public User (int id,String nom, String prenom, String email, String password, String role, Timestamp date_created) {
	 this.id = id;
	 this.nom = nom;
	 this.prenom = prenom;
	 this.email = email;
	 this.password = password;
	 this.role = role;
	 this.date_created = date_created;
 }

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getNom() {
	return nom;
}

public void setNom(String nom) {
	this.nom = nom;
}

public String getPrenom() {
	return prenom;
}

public void setPrenom(String prenom) {
	this.prenom = prenom;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}
 
 }