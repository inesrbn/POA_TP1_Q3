import ApplicationClient;

package tests;
public class TestScenario {

    public static void main(String[] args) {

        ApplicationClient client = new ApplicationClient();

        client.initialise("commandes.txt", "sortie.txt");

        client.scenario();

        System.out.println("Scenario terminé !");
    }
}