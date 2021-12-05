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
 * Auteurs : Marco Dorigo et Thomas Stützle
 * Liens:
 * -> https://mitpress.mit.edu/books/ant-colony-optimization
 * -> http://www.aco-metaheuristic.org/
 * -> https://github.com/thomasnield/traveling_salesman_demo
 *
 * Cet algorithme présente l'implémentation de l'ACO pour les problèmes TSP.
 */

public class Program {

    public static void main(String[] args) throws Exception {

        String tspPath = (new File(".")).getCanonicalPath();
        tspPath = Paths.get(tspPath, "tsp").toAbsolutePath().toString();
        String tspFiles[] = {"lin318.tsp", "att532.tsp", "eil51.tsp", "pcb1173.tsp", "pr2392.tsp"};

        Program app = new Program();
        // Test more simulations

        //Cette boucle nous permet de pouvoir faire le test de nos différents fichiers .tsp
        for(String tspFile : tspFiles) {
            System.out.println("\nProblem: " + tspFile);
            app.startApplication(tspPath, tspFile);
        }
    }

    // Main part of the algorithm

    //Voici la méthode qui fait le démarrage de notre algorithme
    public void startApplication(String path, String file) {

        // Create a TSP instance from file with .tsp extension

        //On fait la création d'instance TSP en utilisant en variable d'entrées le fichier .tsp
        Environment environment = new Environment(TspReader.getDistances(path, file));
        Statistics statistics = new Statistics(file, environment, TspReader.getCoordinates(path, file));

        // Startup part

        //ici on exécute les fonctions suivantes : generateNearestNeighborList(), generateAntPopulation(), generateEnvironment()
        //qui sont localisées dans la classe environment
        environment.generateNearestNeighborList();
        environment.generateAntPopulation();
        environment.generateEnvironment();

        // Repeat the ants behavior by n times

        //On fait la répétition du comportement des fourmis n fois
        int n = 0;
        while(n < Parameters.iterationsMax) {
            environment.constructSolutions();
            environment.updatePheromone();
            statistics.calculateStatistics(n);
            n++;
        }

        //Ici on fait une pause pour montrer le résultat final à l'écran.
        try { Thread.sleep(100000); } catch (Exception ex) {}
        statistics.close();
        System.out.println("Finished");
    }

}
