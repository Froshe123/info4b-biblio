import java.io.Serializable;

public class Livre implements Serializable {
    private static final long serialVersionUID = 1L;

    private int uid;
    private String titre;
    private int nbPages;
    private String type;

    public Livre(int uid, String titre, int nbPages, String type) {
        this.uid = uid;
        this.titre = titre;
        this.nbPages = nbPages;
        this.type = type;
    }

    public int getUid() {
        return uid;
    }

    public String getTitre() {
        return titre;
    }

    public int getNbPages() {
        return nbPages;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "uid=" + uid + ", titre='" + titre + "', pages=" + nbPages + ", type='" + type + "'";
    }
}
