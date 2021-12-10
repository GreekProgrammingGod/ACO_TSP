package aco;


import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
/**
 * ACO - Méta-heuristique d'optimisation des colonies de fourmis
 *
 * Ouvrage de référence : Ant Colony Optimization.
 * Auteurs : Marco Dorigo, Thomas Stützle et João Pedro Schmitt
 * Liens:
 * -> https://github.com/schmittjoaopedro/aco-tsp-java
 * -> https://mitpress.mit.edu/books/ant-colony-optimization
 * -> http://www.aco-metaheuristic.org/
 * -> https://github.com/thomasnield/traveling_salesman_demo
 * -> https://github.com/diogo-fernan/aco
 *
 * Cet algorithme présente l'implémentation de l'ACO pour les problèmes TSP.
 */

public class Main {

    public static void main(String[] args) throws Exception {

        String tspPath = (new File(".")).getCanonicalPath();
        tspPath = Paths.get(tspPath, "tsp").toAbsolutePath().toString();
        String tspFiles[] = {"lin318.tsp", "att532.tsp", "eil51.tsp", "pcb1173.tsp", "pr2392.tsp"};

        Main app = new Main();


        //Cette boucle nous permet de pouvoir faire le test de nos différents fichiers .tsp
        for(String tspFile : tspFiles) {
            System.out.println("\nProblem: " + tspFile);
            app.startApplication(tspPath, tspFile);
        }
    }


    //Voici la méthode qui fait le démarrage de notre algorithme
    public void startApplication(String path, String file) {


        //On fait la création d'instance TSP en utilisant en variable d'entrées le fichier .tsp
        Environnement environnement = new Environnement(LecteurTsp.getDistances(path, file));
        Statistiques statistiques = new Statistiques(file, environnement, LecteurTsp.getCoordinates(path, file));

        //ici on exécute les fonctions suivantes : generateNearestNeighborList(), generateAntPopulation(), generateEnvironment()
        //qui sont localisées dans la classe environment
        environnement.generateNearestNeighborList();
        environnement.generateAntPopulation();
        environnement.generateEnvironment();



        //On fait la répétition du comportement des fourmis n fois
        int n = 0;
        while(n < Parametres.iterationsMax) {
            environnement.constructSolutions();
            environnement.updatePheromone();
            statistiques.calculateStatistics(n);
            n++;
        }

        //Ici on fait une pause pour montrer le résultat final à l'écran.
        try { Thread.sleep(100000); } catch (Exception ex) {}
        statistiques.fermer();
        System.out.println("Finished");
    }

}
