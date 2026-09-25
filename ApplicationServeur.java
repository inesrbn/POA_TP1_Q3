package Q3;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class ApplicationServeur {

    private ServerSocket serverSocket;
    private Map<String, Object> objetsCrees;

    public ApplicationServeur(int port) {
        this.objetsCrees = new HashMap<>();
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Serveur démarré sur le port " + port);
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du serveur sur le port " + port);
            e.printStackTrace();
        }
    }

    public void aVosOrdres() {
        while (true) {
            try (Socket socketClient = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(socketClient.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream())) {

                Commande commande = (Commande) in.readObject();
                traiteCommande(commande);
                
                // Renvoi de la commande modifiée (avec le résultat) au client
                out.writeObject(commande);
                out.flush();

            } catch (Exception e) {
                System.err.println("Erreur de communication avec le client : " + e.getMessage());
            }
        }
    }

    public void traiteCommande(Commande uneCommande) {
        String type = uneCommande.getType();
        String[] params = uneCommande.getParametres();
        Object resultat = null;

        try {
            switch (type) {
                case "compilation":
                    // params[0] = fichiers sources, params[1] = destination (si séparé par #)
                    traiterCompilation(params[0] + (params.length > 1 ? " -d " + params[1] : ""));
                    resultat = "Compilation terminée avec succès.";
                    break;
                case "chargement":
                    traiterChargement(params[0]);
                    resultat = "Classe " + params[0] + " chargée avec succès.";
                    break;
                case "creation":
                    Class<?> classe = Class.forName(params[0]);
                    traiterCreation(classe, params[1]);
                    resultat = "Objet " + params[1] + " instancié avec succès.";
                    break;
                case "lecture":
                    Object objLecture = objetsCrees.get(params[0]);
                    resultat = traiterLecture(objLecture, params[1]);
                    break;
                case "ecriture":
                    Object objEcriture = objetsCrees.get(params[0]);
                    traiterEcriture(objEcriture, params[1], params[2]);
                    resultat = "Attribut " + params[1] + " modifié avec succès.";
                    break;
                case "fonction":
                    Object objAppel = objetsCrees.get(params[0]);
                    // Si des arguments sont passés (params[2] existe), on les sépare
                    String argChaine = params.length > 2 ? params[2] : "";
                    
                    // On découpe la chaîne des arguments passée par le client
                    String[] argsBruts = argChaine.isEmpty() ? new String[0] : argChaine.split(",");
                    String[] types = new String[argsBruts.length];
                    Object[] valeurs = new Object[argsBruts.length];
                    
                    for (int i = 0; i < argsBruts.length; i++) {
                        String[] typeEtValeur = argsBruts[i].split(":");
                        types[i] = typeEtValeur[0];
                        valeurs[i] = typeEtValeur[1];
                    }
                    resultat = traiterAppel(objAppel, params[1], types, valeurs);
                    if (resultat == null) resultat = "Appel de " + params[1] + " exécuté.";
                    break;
                default:
                    resultat = "Commande non reconnue.";
            }
        } catch (Exception e) {
            resultat = "Erreur d'exécution: " + e.getMessage();
            e.printStackTrace();
        }

        uneCommande.setResultat(resultat);
    }

    public Object traiterLecture(Object pointeurObjet, String attribut) throws Exception {
        Class<?> clazz = pointeurObjet.getClass();
        try {
            // Tente un accès public direct
            Field field = clazz.getField(attribut);
            return field.get(pointeurObjet);
        } catch (NoSuchFieldException e) {
            // Attribut potentiellement privé : on cherche le getter
            String getterName = "get" + attribut.substring(0, 1).toUpperCase() + attribut.substring(1);
            Method getter = clazz.getMethod(getterName);
            return getter.invoke(pointeurObjet);
        }
    }

    public void traiterEcriture(Object pointeurObjet, String attribut, Object valeur) throws Exception {
        Class<?> clazz = pointeurObjet.getClass();
        String valStr = (String) valeur;

        try {
            Field field = clazz.getField(attribut);
            field.set(pointeurObjet, convertirValeur(field.getType(), valStr));
        } catch (NoSuchFieldException e) {
            // Cherche le setter
            String setterName = "set" + attribut.substring(0, 1).toUpperCase() + attribut.substring(1);
            // On parcourt les méthodes pour trouver le bon setter (pour gérer les différents types de paramètres)
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    method.invoke(pointeurObjet, convertirValeur(paramType, valStr));
                    return;
                }
            }
            throw new NoSuchMethodException("Setter non trouvé pour l'attribut: " + attribut);
        }
    }

    public void traiterCreation(Class<?> classeDeLobjet, String identificateur) throws Exception {
        Object instance = classeDeLobjet.getDeclaredConstructor().newInstance();
        objetsCrees.put(identificateur, instance);
    }

    public void traiterChargement(String nomQualifie) throws Exception {
        Class.forName(nomQualifie);
    }

    public void traiterCompilation(String cheminRelatifFichierSource) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new Exception("Le compilateur Java n'est pas disponible. Assurez-vous d'utiliser un JDK.");
        }
        
        // Sépare les fichiers et les options par espace
        String[] compilerArgs = cheminRelatifFichierSource.replace(",", " ").split(" ");
        int res = compiler.run(null, null, null, compilerArgs);
        
        if (res != 0) {
            throw new Exception("Erreur lors de la compilation des fichiers : " + cheminRelatifFichierSource);
        }
    }

    public Object traiterAppel(Object pointeurObjet, String nomFonction, String[] types, Object valeursObj) throws Exception {
        Class<?> clazz = pointeurObjet.getClass();
        Object[] valeursBrutes = (Object[]) valeursObj;
        
        Class<?>[] classesParametres = new Class<?>[types.length];
        Object[] argumentsReels = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            String typeStr = types[i];
            String valStr = (String) valeursBrutes[i];

            // Gérer les arguments d'objets (ex: ID(mathilde))
            if (valStr.startsWith("ID(") && valStr.endsWith(")")) {
                String id = valStr.substring(3, valStr.length() - 1);
                argumentsReels[i] = objetsCrees.get(id);
                classesParametres[i] = Class.forName(typeStr);
            } else {
                // Types de base
                classesParametres[i] = obtenirClasseDepuisString(typeStr);
                argumentsReels[i] = convertirValeur(classesParametres[i], valStr);
            }
        }

        Method methode = clazz.getMethod(nomFonction, classesParametres);
        return methode.invoke(pointeurObjet, argumentsReels);
    }

    // --- Utilitaires de conversion ---

    private Object convertirValeur(Class<?> type, String valeur) {
        if (type == String.class) return valeur;
        if (type == int.class || type == Integer.class) return Integer.parseInt(valeur);
        if (type == float.class || type == Float.class) return Float.parseFloat(valeur);
        if (type == double.class || type == Double.class) return Double.parseDouble(valeur);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(valeur);
        return valeur;
    }

    private Class<?> obtenirClasseDepuisString(String type) throws ClassNotFoundException {
        switch (type) {
            case "int": return int.class;
            case "float": return float.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            case "java.lang.String": return String.class;
            default: return Class.forName(type);
        }
    }

}