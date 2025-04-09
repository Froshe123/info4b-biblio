import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;

public class ServeurPrincipal {
    private static final Object verrou = new Object();

    public static void main(String[] args) {
        int port = 12345;

        Map<String, String> comptesUtilisateurs = chargerComptes();
        List<Livre> listeLivres = chargerLivres();

        try (ServerSocket serveur = new ServerSocket(port)) {
            System.out.println("Serveur prêt sur le port " + port);

            while(true) {
                Socket socketClient = serveur.accept();
                System.out.println("Un utilisateur est connecté : " + socketClient.getRemoteSocketAddress());

                GestionnaireClient gc = new GestionnaireClient(socketClient, comptesUtilisateurs, listeLivres, verrou);
                gc.run();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> chargerComptes() {
        File f = new File("users.db");
        if(!f.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if(obj instanceof Map) {
                return (Map<String, String>) obj;
            }
        } catch(IOException | ClassNotFoundException e) {
        }
        return new HashMap<>();
    }



    @SuppressWarnings("unchecked")
    private static List<Livre> chargerLivres() {
        File f = new File("livres.db");
        if(!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if(obj instanceof List) {
                return (List<Livre>) obj;
            }
        } catch(IOException | ClassNotFoundException e) {
        }
        return new ArrayList<>();
    }
}
