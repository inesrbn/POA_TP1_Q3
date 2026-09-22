import Commande;

package tests;
public class TestCommande {

    public static void main(String[] args) {

        String[] parametres = {
            "ca.uqac.registraire.Cours",
            "8inf853"
        };

        Commande commande = new Commande("creation", parametres);

        System.out.println("Type : " + commande.getType());

        System.out.println("Paramètre 1 : "
                + commande.getParametres()[0]);

        System.out.println("Paramètre 2 : "
                + commande.getParametres()[1]);

        commande.setResultat("Création réussie");

        System.out.println("Résultat : "
                + commande.getResultat());
    }
}