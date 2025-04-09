import java.io.Serializable;
import java.util.List;

public class Reponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean succes;
    private String message;
    private List<Livre> listeLivres;

    public Reponse(boolean succes) {
        this.succes = succes;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Livre> getListeLivres() {
        return listeLivres;
    }

    public void setListeLivres(List<Livre> listeLivres) {
        this.listeLivres = listeLivres;
    }
}
