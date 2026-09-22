import java.io.Serializable;

public class Commande implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String[] parametres;
    private Object resultat;

    public Commande(String type, String[] parametres) {
        this.type = type;
        this.parametres = parametres;
        this.resultat = null;
    }

    public String getType() {
        return type;
    }

    public String[] getParametres() {
        return parametres;
    }

    public Object getResultat() {
        return resultat;
    }

    public void setResultat(Object resultat) {
        this.resultat = resultat;
    }
}