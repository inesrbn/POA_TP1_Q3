import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.*;
import java.net.Socket;

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

    // initialise : ouvre les différents fichiers de lecture et écriture
    public void initialise(String nomFichierCommandes, String nomFichierSortie) {
        try {
            fichierCommandes = new BufferedReader(new FileReader(nomFichierCommandes));
            fichierSortie = new BufferedWriter(new FileWriter(nomFichierSortie));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * prend une Commande dûment formatée, et la fait exécuter par le serveur.
     * Le résultat de l’exécution est retournée.
     * Si la commande ne retourne pas de résultat, on retourne null.
     * Chaque appel doit ouvrir une connexion, exécuter, et fermer la connexion.
     * ouvrir: ApplicationServeur(int port)
     * Si vous le souhaitez, vous pourriez écrire six fonctions spécialisées, une par type de commande
     * décrit plus haut, qui seront appelées par traiteCommande(Commande uneCommande)
     */
    public Object traiteCommande(Commande uneCommande) {
        if (uneCommande == null) {
            return null;
        }
        try (Socket socket = new Socket(nomServeur, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(uneCommande);
            out.flush();
            Commande reponse = (Commande) in.readObject();

            return reponse.getResultat();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erreur de communication avec le serveur", e);
        }
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

    /** Ferme les fichiers ouverts par initialise. */
    public void termine() {
        try {
            if (fichierCommandes != null) fichierCommandes.close();
            if (fichierSortie != null) fichierSortie.close();
        } catch (IOException e) {
            System.err.println("Erreur de fermeture des fichiers : " + e.getMessage());
        }
    }

    /**
     * programme principal. Prend 4 arguments: 1) hostname du serveur, 2) numéro de port,
     * 3) nom fichier commandes, et 4) nom fichier sortie. Cette méthode doit créer une
     * instance de la classe ApplicationClient, l’initialiser, puis exécuter le scénario
     */
    public static void main(String[] args) {
        ApplicationClient client = new ApplicationClient();

        // Setup des infos du serveur
        client.nomServeur = args[0];
        client.port = Integer.parseInt(args[1]);

        // Initialisation du client
        client.initialise(args[2], args[3]);

        // Exécution du scénario
        client.scenario();

        //
        client.termine();
        System.out.println("Traitements termines, resultats dans " + args[3]);
    }
}