package tests;
import java.io.BufferedReader;
import java.io.FileReader;

import ApplicationClient;
import Commande;

public class TestClient {

    public static void main(String[] args) {

        try {
            BufferedReader fichier =
                    new BufferedReader(new FileReader("commandes.txt"));

            ApplicationClient client = new ApplicationClient();

            Commande commande = client.saisisCommande(fichier);

            System.out.println("Type : " + commande.getType());

            for (String parametre : commande.getParametres()) {
                System.out.println("Paramètre : " + parametre);
            }

            fichier.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}