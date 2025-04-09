import java.io.Serializable;

public class Requete implements Serializable {
    private static final long serialVersionUID = 1L;

    // Par exemple : "register", "auth", "ajouterLivre", "retirerLivre", "rechercherLivre", "listerLivres", "exit"
    private String operation;

    // Pour l'authentification
    private String nomUtilisateur;
    private String motDePasse;

    // Un livre
    private Livre livre;

    public Requete(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }
}
