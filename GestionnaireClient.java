import java.net.Socket;
import java.io.*;
import java.util.*;

public class GestionnaireClient implements Runnable {

    private Socket socketClient;
    private Map<String, String> comptesUtilisateurs;
    private List<Livre> listeLivres;
    private final Object verrou;
    private boolean authentifie = false;

    public GestionnaireClient(Socket socketClient,
                             Map<String, String> comptesUtilisateurs,
                             List<Livre> listeLivres,
                             Object verrou) {
        this.socketClient = socketClient;
        this.comptesUtilisateurs = comptesUtilisateurs;
        this.listeLivres = listeLivres;
        this.verrou = verrou;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream fluxSortie = new ObjectOutputStream(socketClient.getOutputStream());
            ObjectInputStream fluxEntree = new ObjectInputStream(socketClient.getInputStream());
        ) {
            while(true) {
                Requete req = (Requete) fluxEntree.readObject();
                if(req == null) break;

                String op = req.getOperation();
                Reponse rep;

                if(!authentifie && !(op.equals("register") || op.equals("auth") || op.equals("exit"))) {
                    rep = new Reponse(false);
                    rep.setMessage("Veuillez vous connecter d'abord.");
                    fluxSortie.writeObject(rep);
                    fluxSortie.flush();
                    continue;
                }

                if(op.equals("exit")) {
                    rep = new Reponse(true);
                    rep.setMessage("Au revoir.");
                    fluxSortie.writeObject(rep);
                    fluxSortie.flush();
                    return;
                }

                switch(op) {
                    case "register":
                        rep = gererRegister(req);
                        break;
                    case "auth":
                        rep = gererAuth(req);
                        break;
                    case "ajouterLivre":
                        rep = gererAjouterLivre(req);
                        break;
                    case "retirerLivre":
                        rep = gererRetirerLivre(req);
                        break;
                    case "rechercherLivre":
                        rep = gererRechercherLivre(req);
                        break;
                    case "listerLivres":
                        rep = gererListerLivres();
                        break;
                    default:
                        rep = new Reponse(false);
                        rep.setMessage("Opération inconnue.");
                }

                fluxSortie.writeObject(rep);
                fluxSortie.flush();
            }
        } catch(Exception e) {
            System.out.println("Client parti ou erreur: " + e.getMessage());
        } finally {
            try { socketClient.close(); } catch(IOException e){}
        }
    }

    private void sauvegarderLivres() {
        synchronized(verrou) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("livres.db"))) {
                oos.writeObject(listeLivres);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sauvegarderComptes() {
        synchronized(verrou) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.db"))) {
                oos.writeObject(comptesUtilisateurs);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Reponse gererRegister(Requete req) {
        Reponse rep = new Reponse(false);
        String nom = req.getNomUtilisateur();
        String mdp = req.getMotDePasse();
        if(nom == null || mdp == null || nom.isEmpty() || mdp.isEmpty()) {
            rep.setMessage("Informations invalides.");
            return rep;
        }
        synchronized(verrou) {
            if(comptesUtilisateurs.containsKey(nom)) {
                rep.setMessage("Ce compte existe déjà.");
                return rep;
            }
            comptesUtilisateurs.put(nom, mdp);
            sauvegarderComptes();
        }
        rep.setSucces(true);
        rep.setMessage("Compte créé.");
        return rep;
    }

    private Reponse gererAuth(Requete req) {
        Reponse rep = new Reponse(false);
        String nom = req.getNomUtilisateur();
        String mdp = req.getMotDePasse();
        synchronized(verrou) {
            if(comptesUtilisateurs.containsKey(nom) && comptesUtilisateurs.get(nom).equals(mdp)) {
                authentifie = true;
                rep.setSucces(true);
                rep.setMessage("Connexion réussie.");
            } else {
                rep.setMessage("Identifiants invalides.");
            }
        }
        return rep;
    }

    private Reponse gererAjouterLivre(Requete req) {
        Reponse rep = new Reponse(true);
        Livre lv = req.getLivre();
        synchronized(verrou) {
            boolean existeDeja = listeLivres.stream()
                .anyMatch(l -> l.getUid() == lv.getUid());

        if (existeDeja) {
            rep.setSucces(false);
            rep.setMessage("Erreur : un livre avec cet UID existe déjà.");
            return rep;
        }
            listeLivres.add(lv);
            sauvegarderLivres();
        }
        rep.setMessage("Livre ajouté.");
        return rep;
    }

    private Reponse gererRetirerLivre(Requete req) {
        Reponse rep = new Reponse(true);
        Livre critere = req.getLivre();
        synchronized(verrou) {
            boolean retire = listeLivres.removeIf(l -> correspond(l, critere));
            if(retire) {
                sauvegarderLivres();
                rep.setMessage("Un ou plusieurs livres retirés.");
            } else {
                rep.setSucces(false);
                rep.setMessage("Aucun livre correspondant.");
            }
        }
        return rep;
    }

    private boolean correspond(Livre l, Livre c) {
        if(c.getUid() != 0 && l.getUid() != c.getUid()) {
            return false;
        }
        if(!c.getTitre().isEmpty() && !l.getTitre().equalsIgnoreCase(c.getTitre())) {
            return false;
        }
        if(c.getNbPages() != 0 && l.getNbPages() != c.getNbPages()) {
            return false;
        }
        if(!c.getType().isEmpty() && !l.getType().equalsIgnoreCase(c.getType())) {
            return false;
        }
        return true;
    }

    private Reponse gererRechercherLivre(Requete req) {
        Reponse rep = new Reponse(true);
        Livre crit = req.getLivre();
        List<Livre> resultat = new ArrayList<>();
        synchronized(verrou) {
            for(Livre l : listeLivres) {
                if(correspond(l, crit)) {
                    resultat.add(l);
                }
            }
        }
        rep.setListeLivres(resultat);
        rep.setMessage("Trouvé " + resultat.size() + " livre(s).");
        return rep;
    }

    private Reponse gererListerLivres() {
        Reponse rep = new Reponse(true);
        List<Livre> copie;
        synchronized(verrou) {
            copie = new ArrayList<>(listeLivres);
        }
        rep.setListeLivres(copie);
        rep.setMessage("Nombre de livres : " + copie.size());
        return rep;
    }
}
