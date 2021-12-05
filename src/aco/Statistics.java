package aco;

/**
 * Calcule les statistiques de l'évolution de l'algorithme
 */
public class Statistics {

    /**
     * L'environnement des agents à analyser
     */
    private Environment environment;

    /**
     * Représente le coût du meilleur chemin retrouver àdate (peut
     * changer à la fin de n'importe quelle itération)
     */
    private double bestSoFar = Double.MAX_VALUE;

    /**
     * Représente le meilleur chemin retrouvé à date (suite de noeuds)
     */
    private int[] bestTourSoFar;

    /**
     *  Représente le composant qu'on utilise pour visualiser
     *  graphiquementl'évolution de l'algorithme (Java Swing)
     */
    private Visualizer visualizer;

    /**
     * Représente le fichier actuel que l'algorithme est en train de
     * résoudre(parmi les fichiers dans le répertoire tsp)
     */
    private String tspFile;
    /**
     * Associe un environnement et les coordonnées des noeuds du graphe à dessiner
     *
     * @param environment
     * @param coordinates
     */
    public Statistics(String tspFile, Environment environment, double[][] coordinates) {
        this.environment = environment;
        this.visualizer = new Visualizer(coordinates);
        this.tspFile = tspFile;
    }

    /**
     * Pour chaque itération, obtenez le meilleur, le pire et le coût moyen du tour
     * de tous les circuits construits par les fourmis, si une amélioration
     * a été détectée, afficher les valeurs du meilleur tour à date.
     * @param phase
     */
    public void calculateStatistics(int phase) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Ant bestAnt = null;
        for(Ant ant : environment.getAnts()) {
            if(ant.getTourCost() < min) {
                min = ant.getTourCost();
                bestAnt = ant;
            }
            if(ant.getTourCost() > max) {
                max = ant.getTourCost();
            }
            total += ant.getTourCost();
        }
        if(min < bestSoFar) {
            bestSoFar = min;
            bestTourSoFar = bestAnt.getTour().clone();
            String stats = String.format("%s -> Min(%.1f) Phase(%d) Max(%.1f) Mean(%.1f)\n", tspFile, min, phase, max, (total / environment.getAntPopSize()));
            String message = "[" + bestTourSoFar[0];
            for(int i = 1; i < bestTourSoFar.length - 1; i++) {
                message += "->" + bestTourSoFar[i];
            }
            message += "]";
            System.out.println(message);
            visualizer.setStat(stats);
            visualizer.draw(bestTourSoFar);
            try { Thread.sleep(500); } catch (Exception ex) {}
        }
    }

    /**
     * Fermeture de la visualisation
     */
    public void close() {
        this.visualizer.dispose();
    }

}
