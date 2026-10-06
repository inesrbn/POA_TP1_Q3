# POA_TP1_Q3

## Exécution du programme

- Step 1: Clone le repo
- Step 2: Ouvrir un terminal dans le répertoire du projet et exécuter la commande : `javac -d build Commande.java ApplicationClient.java ApplicationServeur.java`
- Step 3: Ouvrir un terminal pour le **serveur** dans le répertoire du projet et exécuter la commande : `java -cp build ApplicationServeur 8080 ./src ./classes traces.txt`
- Step 4: Ouvrir un terminal pour le **client** dans le répertoire du projet et exécuter la commande : `java -cp build ApplicationClient localhost 8080 commandes.txt sortie.txt`
