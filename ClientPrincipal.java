import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class ClientPrincipal {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (
            Socket socket = new Socket(host, port);
            ObjectOutputStream fluxSortie = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fluxEntree = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);
        ) {
            boolean enCours = true;
            boolean connecte = false;

            while(!connecte && enCours) {
                System.out.println("\n=== Menu Non Auth ===");
                System.out.println("1. Créer un compte");
                System.out.println("2. Se connecter");
                System.out.println("3. Quitter");
                System.out.print("Choix : ");
                String choix = sc.nextLine();

                switch(choix) {
                    case "1":
                        gererRegister(sc, fluxSortie, fluxEntree);
                        break;
                    case "2":
                        connecte = gererAuth(sc, fluxSortie, fluxEntree);
                        break;
                    case "3":
                        gererExit(fluxSortie, fluxEntree);
                        enCours = false;
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            }

            while(connecte && enCours) {
                System.out.println("\n=== Menu Bibliothèque ===");
                System.out.println("1. Ajouter un livre");
                System.out.println("2. Retirer un livre (uid ou titre etc.)");
                System.out.println("3. Rechercher un livre");
                System.out.println("4. Lister tous les livres");
                System.out.println("5. Quitter");
                System.out.print("Choix : ");
                String choix = sc.nextLine();

                switch(choix) {
                    case "1":
                        gererAjouterLivre(sc, fluxSortie, fluxEntree);
                        break;
                    case "2":
                        gererRetirerLivre(sc, fluxSortie, fluxEntree);
                        break;
                    case "3":
                        gererRechercherLivre(sc, fluxSortie, fluxEntree);
                        break;
                    case "4":
                        ListerLivres(fluxSortie, fluxEntree);
                        break;
                    case "5":
                        gererExit(fluxSortie, fluxEntree);
                        enCours = false;
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            }

            System.out.println("Fin du client.");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void gererRegister(Scanner sc,
                                      ObjectOutputStream fluxSortie,
                                      ObjectInputStream fluxEntree)
                                      throws IOException, ClassNotFoundException {
        Requete req = new Requete("register");
        System.out.print("Nom d'utilisateur : ");
        req.setNomUtilisateur(sc.nextLine());
        System.out.print("Mot de passe : ");
        req.setMotDePasse(sc.nextLine());

        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
    }

    private static boolean gererAuth(Scanner sc,ObjectOutputStream fluxSortie,ObjectInputStream fluxEntree)
        throws IOException, ClassNotFoundException {
        Requete req = new Requete("auth");
        System.out.print("Nom d'utilisateur : ");
        req.setNomUtilisateur(sc.nextLine());
        System.out.print("Mot de passe : ");
        req.setMotDePasse(sc.nextLine());

        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
        return rep.isSucces();
    }

    private static void gererExit(ObjectOutputStream fluxSortie,ObjectInputStream fluxEntree)
        throws IOException, ClassNotFoundException {
        Requete req = new Requete("exit");
        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
    }

    private static void gererAjouterLivre(Scanner sc,
                                          ObjectOutputStream fluxSortie,
                                          ObjectInputStream fluxEntree)
                                          throws IOException, ClassNotFoundException {
        Requete req = new Requete("ajouterLivre");

        System.out.print("UID : ");
        
        int uid = Integer.parseInt(sc.nextLine());
        System.out.print("Titre : ");
        String titre = sc.nextLine();
        System.out.print("Nombre de pages : ");
        int nb = Integer.parseInt(sc.nextLine());
        System.out.print("Type : ");
        String t = sc.nextLine();

        Livre lv = new Livre(uid, titre, nb, t);
        req.setLivre(lv);

        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
    }

    private static void gererRetirerLivre(Scanner sc,
                                          ObjectOutputStream fluxSortie,
                                          ObjectInputStream fluxEntree)
                                          throws IOException, ClassNotFoundException {
        Requete req = new Requete("retirerLivre");

        System.out.println("Remplir les champs voulus (0 ou vide => ignoré).");
        System.out.print("UID : ");
        int uid = Integer.parseInt(sc.nextLine());
        System.out.print("Titre : ");
        String titre = sc.nextLine();
        System.out.print("Nb Pages : ");
        int nbPages = Integer.parseInt(sc.nextLine());
        System.out.print("Type : ");
        String type = sc.nextLine();

        Livre crit = new Livre(uid, titre, nbPages, type);
        req.setLivre(crit);

        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
    }

    private static void gererRechercherLivre(Scanner sc,
                                             ObjectOutputStream fluxSortie,
                                             ObjectInputStream fluxEntree)
                                             throws IOException, ClassNotFoundException {
        Requete req = new Requete("rechercherLivre");

        System.out.println("Champs (0 ou vide => ignoré).");
        System.out.print("UID : ");
        int uid = Integer.parseInt(sc.nextLine());
        System.out.print("Titre : ");
        String titre = sc.nextLine();
        System.out.print("Nb Pages : ");
        int nb = Integer.parseInt(sc.nextLine());
        System.out.print("Type : ");
        String t = sc.nextLine();

        Livre crit = new Livre(uid, titre, nb, t);
        req.setLivre(crit);

        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
        if(rep.isSucces() && rep.getListeLivres() != null) {
            for(Livre l : rep.getListeLivres()) {
                System.out.println("   " + l);
            }
        }
    }

    private static void ListerLivres(ObjectOutputStream fluxSortie,
                                          ObjectInputStream fluxEntree)
                                          throws IOException, ClassNotFoundException {
        Requete req = new Requete("listerLivres");
        fluxSortie.writeObject(req);
        fluxSortie.flush();

        Reponse rep = (Reponse) fluxEntree.readObject();
        System.out.println(">> " + rep.getMessage());
        if(rep.isSucces() && rep.getListeLivres() != null) {
            for(Livre l : rep.getListeLivres()) {
                System.out.println("   " + l);
            }
        }
    }
}
