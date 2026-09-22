import java.io.BufferedReader;
import java.io.BufferedWriter;

public class TestInitialise {

    public static void main(String[] args) {

        ApplicationClient client = new ApplicationClient();

        client.initialise("commandes.txt", "sortie.txt");

        System.out.println("Initialisation terminée !");
    }
}