package aco;


import java.io.File;

import java.nio.file.Paths;

/**
 * ACO - Méta-heuristique d'optimisation des colonies de fourmis
 *
 * Ouvrage de référence : Ant Colony Optimization.
 * Auteurs : Marco Dorigo, Thomas Stützle et João Pedro Schmitt
 * Liens :
 *     → https://github.com/schmittjoaopedro/aco-tsp-java
 *     → https://mitpress.mit.edu/books/ant-colony-optimization
 *     → http://www.aco-metaheuristic.org/
 *     → https://github.com/thomasnield/traveling_salesman_demo
 *     → https://github.com/diogo-fernan/aco
 *
 * Cet algorithme présente l'implémentation de ACO pour les problèmes TSP.
 */

public class Main {

    public static void main(String[] args) throws Exception {

        String cheminFichiersTsp = (new File(".")).getCanonicalPath();
        cheminFichiersTsp = Paths.get(cheminFichiersTsp, "tsp").toAbsolutePath().toString();
        String fichiersTsp[] = {"lin318.tsp", "att532.tsp", "eil51.tsp", "pcb1173.tsp", "pr2392.tsp"};

        Main app = new Main();

        //Cette boucle nous permet de pouvoir faire le test de nos différents fichiers .tsp
        for(String fichierTsp : fichiersTsp) {
            System.out.println("\nProblem: " + fichierTsp);
            app.debutApplication(cheminFichiersTsp, fichierTsp);
        }
    }


    //Voici la méthode qui fait le démarrage de notre algorithme
    public void debutApplication(String cheminFichier, String fichier) {


        //Création d'une instance TSP à partir d'un fichier avec l'extension .tsp
        Environnement environnement = new Environnement(LecteurTsp.getDistances(cheminFichier, fichier));
        Statistiques statistiques = new Statistiques(fichier, environnement, LecteurTsp.getCoordonnees(cheminFichier, fichier));

        //Ici on exécute les fonctions suivantes : generateNearestNeighborList(), generateAntPopulation(),
        //generateEnvironment() qui sont localisées dans la classe environment
        environnement.genererListeDesVoisinsLesPlusProches();
        environnement.genererPopulationFourmis();
        environnement.genererEnvironnement();



        //On fait la répétition du comportement des fourmis n fois (paramètre iterationsMax)
        int n = 0;
        while(n < Parametres.iterationsMax) {
            environnement.construireSolutions();
            environnement.miseajourPheromone();
            statistiques.calculerStats(n);
            n++;
        }

        //Ici on fait une pause pour montrer le résultat final à l'écran
        try { Thread.sleep(100000); } catch (Exception ex) {}
        statistiques.fermer();
        System.out.println("Fin de l'environnement du fichier " + fichier);
    }

}
