package aco;

/**
 * Calcule les statistiques de l'évolution de l'algorithme
 */
public class Statistiques {

    /**
     * L'environnement des agents à analyser
     */
    private Environnement environnement;

    /**
     * Représente le coût du meilleur chemin retrouver à date (peut
     * changer à la fin de n'importe quelle itération)
     */
    private double coutMeilleurSequenceADate = Double.MAX_VALUE;

    /**
     * Représente le meilleur chemin retrouvé à date (suite de noeuds)
     */
    private int[] meilleureSequenceADate;

    /**
     *  Représente le composant qu'on utilise pour visualiser
     *  graphiquementl'évolution de l'algorithme (Java Swing)
     */
    private Visualiseur visualiseur;

    /**
     * Représente le fichier actuel que l'algorithme est en train de
     * résoudre(parmi les fichiers dans le répertoire tsp)
     */
    private String fichierTsp;
    /**
     * Associe un environnement et les coordonnées des noeuds du graphe à dessiner
     *
     * @param environnement
     * @param coordonnees
     */
    public Statistiques(String tspFile, Environnement environnement, double[][] coordonnees) {
        this.environnement = environnement;
        this.visualiseur = new Visualiseur(coordonnees);
        this.fichierTsp = tspFile;
    }

    /**
     * Pour chaque itération, obtenez le meilleur, le pire et le coût moyen du tour
     * de tous les circuits construits par les fourmis, si une amélioration
     * a été détectée, afficher les valeurs du meilleur tour à date.
     * @param phase
     */
    public void calculerStats(int phase) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Fourmi meilleureFourmi = null;
        for(Fourmi fourmi : environnement.getAnts()) {
            if(fourmi.getDistanceTotal() < min) {
                min = fourmi.getDistanceTotal();
                meilleureFourmi = fourmi;
            }
            if(fourmi.getDistanceTotal() > max) {
                max = fourmi.getDistanceTotal();
            }
            total += fourmi.getDistanceTotal();
        }
        if(min < coutMeilleurSequenceADate) {
            coutMeilleurSequenceADate = min;
            meilleureSequenceADate = meilleureFourmi.getSequenceNoeuds().clone();
            String stats = String.format("%s -> Min(%.1f) Phase(%d) Max(%.1f) Moyenne(%.1f)\n", fichierTsp, min, phase, max, (total / environnement.getTaillePopulationFourmis()));
            String message = "[" + meilleureSequenceADate[0];
            for(int i = 1; i < meilleureSequenceADate.length - 1; i++) {
                message += "->" + meilleureSequenceADate[i];
            }
            message += "]";
            System.out.println(message);
            visualiseur.setStat(stats);
            visualiseur.draw(meilleureSequenceADate);
            try { Thread.sleep(500); } catch (Exception ex) {}
        }
    }

    /**
     * Fermeture de la visualisation
     */
    public void fermer() {
        this.visualiseur.dispose();
    }

}
