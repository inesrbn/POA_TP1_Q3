import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class ApplicationClient {

    private BufferedReader fichierCommandes;
    private BufferedWriter fichierSortie;

    private String nomServeur;
    private int port;

    /*
    prend le fichier contenant la liste des commandes, et le charge dans une
    variable du type Commande qui est retournée
    */
    public Commande saisisCommande(BufferedReader reader) {
        try {
            String ligne = reader.readLine();
            
            if (ligne == null){
                return null;
            }

            String[] morceaux = ligne.split("#", -1);

            String type = morceaux[0];

            String[] parametres = new String[morceaux.length - 1];

            for (int i = 1; i < morceaux.length; i++) {
                parametres[i - 1] = morceaux[i];
            }

            return new Commande(type, parametres);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    //initialise : ouvre les différents fichiers de lecture et écriture
    public void initialise(String nomFichierCommandes, String nomFichierSortie) {
        try {
            fichierCommandes = new BufferedReader(new FileReader(nomFichierCommandes));
            fichierSortie = new BufferedWriter(new FileWriter(nomFichierSortie));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object traiteCommande(Commande uneCommande) {
        return null;
    }

    /*
    cette méthode vous sera fournie plus tard. Elle indiquera la séquence d’étapes à exécuter
    pour le test. Elle fera des appels successifs à saisisCommande(BufferedReader fichier) et
    traiteCommande(Commande uneCommande).
    */
    public void scenario() {
        try {
            fichierSortie.write("Debut des traitements:");
            fichierSortie.newLine();

            Commande prochaine = saisisCommande(fichierCommandes);

            while (prochaine != null) {

                fichierSortie.write(
                    "\tTraitement de la commande " + prochaine + " ..."
                );
                fichierSortie.newLine();

                Object resultat = traiteCommande(prochaine);

                fichierSortie.write(
                    "\t\tResultat: " + resultat
                );
                fichierSortie.newLine();

                prochaine = saisisCommande(fichierCommandes);
            }

            fichierSortie.write("Fin des traitements");
            fichierSortie.newLine();

            fichierSortie.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}