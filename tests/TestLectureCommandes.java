package tests;
import java.io.BufferedReader;
import java.io.FileReader;

import ApplicationClient;
import Commande;

public class TestLectureCommandes {

    public static void main(String[] args) {

        try {
            BufferedReader fichier =
                    new BufferedReader(new FileReader("commandes.txt"));

            ApplicationClient client = new ApplicationClient();

            Commande commande;

            while ((commande = client.saisisCommande(fichier)) != null) {

                System.out.println("Type : " + commande.getType());

                String[] parametres = commande.getParametres();

                for (int i = 0; i < parametres.length; i++) {
                    System.out.println(
                            "  Paramètre " + i + " : " + parametres[i]
                    );
                }

                System.out.println("--------------------");
            }

            fichier.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}