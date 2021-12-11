package aco;

/**
 * Les paramètres global qui vont permettent d'ajuster ACO
 */
public class Parametres {

    /**
     * Taux d'évaporation des pheromones dans les différents trajets (rho)
     */
    public static double tauxEvaporationPheromones = 0.5;

    /**
     * L'importance des pheromones (alpha)
     */
    public static double importanceDesPheromones = 1.0;

    /**
     * L'importance de l'heuristique (beta)
     */
    public static double importanceDeHeuristique = 2.0;

    /**
     * Nombre de fourmis agents
     */
    public static int taillePopulationFourmis = 60;

    /**
     * La taille de la liste de chaque voisin pour chaque ville
     */
    public static int tailleDeLaListeDesVoisinsLesPlusProches = 20;

    /**
     * Nombre d'itérations pour trouver la bonne solution
     */
    public static int iterationsMax = 450;

}
