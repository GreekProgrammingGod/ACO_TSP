package aco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * represente l'espace de solution, les noeuds et les arcs du graphe a optimiser et la quantite de pheromones
 * laisses par les fourmis
 */
public class Environnement {

    /**
     * valeur pour la quantite initiale de pheromones
     */
    private double cheminInitiale;

    /**
     * matrix pour une solution de graphe NxN
     */
    private double[][] graphe;

    /**
     * la matrice NxN trouve le voisin le plus proche sur le graphe
     */
    private int[][] NNList;

    /**
     * indication de la quantite de pheromones laissees par les fourmis
     */
    private double[][] pheromone;

    /**
     * The matrix of NxN storing the value of the best edges calculated using
     * the pheromone amount and the quality of the edges (smaller distances).
     *
     * La matrice
     */
    private double[][] choiceInfo;

    /**
     *
     * Les fourmis
     */
    private Fourmi[] ants;

    /**
     * Necessite une graphe a resoudre
     *
     * @param graphe
     */
    public Environnement(double[][] graphe) {
        super();
        this.graphe = graphe;
    }

    /**
     *
     * Créer une liste des voisins les plus proches nn dans une structure de dimension n x nn ou n est la taille
     * de la population et nn la taille des voisins les plus pres
     */
    public void generateNearestNeighborList() {
        NNList = new int[getNombreDeNoeuds()][getNNSize()];

        //pour chaque noeud dans le graph, on classe les voisins par distance
        //on classe la liste par la taille de nn
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            Integer[] indexNoeud = new Integer[getNombreDeNoeuds()];
            Double[] nodeData = new Double[getNombreDeNoeuds()];
            for(int j = 0; j < getNombreDeNoeuds(); j++) {
                indexNoeud[j] = j;
                nodeData[j] = getCout(i, j);
            }

            // Le bord du sommet courant avec lui-même est laissé comme derniere
            // option à sélectionner dans la liste des voisins les plus proches

            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(indexNoeud, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(nodeData[o1], nodeData[o2]);
                }
            });
            for(int r = 0; r < getNNSize(); r++) {
                NNList[i][r] = indexNoeud[r];
            }
        }
    }

    /**
     * créer une population avec k fourmis pour chercher les solutions à l'environnement
     */
    public void generateAntPopulation() {
        ants = new  Fourmi[getAntPopSize()];
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k] = new  Fourmi(getNombreDeNoeuds(), this);
        }
    }

    /**
     *
     * on crée des phéromones et ont choisi la structure d'information
     * les phéromones représentent la qualité des intersections pour choisir une solution
     * choiceinfo calcule la decision prise par les fourmis afin d'évaluer les chemins à prendre
     *
     * afin de commencer, le pheromones utiliser est la distance entre les villes voisines
     */
    public void generateEnvironment() {
        pheromone = new double[getNombreDeNoeuds()][getNombreDeNoeuds()];
        choiceInfo = new double[getNombreDeNoeuds()][getNombreDeNoeuds()];
        cheminInitiale = 1.0 / (Parametres.rho * ants[0].calculerVoisinPlusProche());
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = i; j < getNombreDeNoeuds(); j++) {
                pheromone[i][j] = cheminInitiale;
                pheromone[j][i] = cheminInitiale;
                choiceInfo[i][j] = cheminInitiale;
                choiceInfo[j][i] = cheminInitiale;
            }
        }
        calculateChoiceInformation();
    }

    /**
     *
     *calculer la probabilite proportionnelle basee sur la quantite de pheromones et sur la distance entre 2 villes,
     * defini par alpha et beta
     */
    public void calculateChoiceInformation() {
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCout(i, j) + 0.1));
                choiceInfo[i][j] = Math.pow(pheromone[i][j], Parametres.alpha) * Math.pow(heuristic, Parametres.beta);
                choiceInfo[j][i] = choiceInfo[i][j];
            }
        }
    }

    /**
     * chaque fourmi va construire une solution dans l'environnement
     */
    public void constructSolutions() {
        // At the first step reset all ants (clearVisited) and put each one
        // in a random vertex of the graph.
        int phase = 0;
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k].nettoyerVisiter();
            ants[k].debuterAUnePositionAleatoire(phase);
        }
        // Make all ants choose the next non visited vertex based in the
        // pheromone trails and heuristic of the edge cost.
        while(phase < getNombreDeNoeuds() - 1) {
            phase++;
            for(int k = 0; k < getAntPopSize(); k++) {
                ants[k].goToNNListAsDecisionRule(phase);
            }
        }
        // Close the circuit and calculate the total cost
        for(int k = 0; k < getAntPopSize(); k++) {
            ants[k].terminerSequence();
        }
    }

    /**
     * calculer la qualite des solutions et le taux d'evaporation des pheromones
     */
    public void updatePheromone() {
        evaporatePheromone();
        for(int k = 0; k < getAntPopSize(); k++) {
            depositPheromone(ants[k]);
        }
        calculateChoiceInformation();
    }

    /**
     * evaporer les pheromones par a exposant (1-rho)
     */
    public void evaporatePheromone() {
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = i; j < getNombreDeNoeuds(); j++) {
                pheromone[i][j] = (1 - Parametres.rho) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    /**
     * For the ant, deposit the amount of pheromone in all edges used in the ant where
     * the amount of pheromone deposited is proportional to the solution quality
     *
     *
     * @param ant
     */
    public void depositPheromone(Fourmi ant) {
        double dTau = 1.0 / ant.getDistanceTotal();
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            int j = ant.getPhaseSequence(i);
            int l = ant.getPhaseSequence(i + 1);
            pheromone[j][l] = pheromone[j][l] + dTau;
            pheromone[l][j] = pheromone[j][l];
        }
    }

    /**
     * retourne le nombre de noeuds
     *
     * @return graphLength
     */
    public int getNombreDeNoeuds() {
        return graphe.length;
    }

    /**
     *
     * retourne la quantite de voisins les plus proches
     *
     * @return nnSize
     */
    public int getNNSize() { return Parametres.NNSize; }

    /**
     * Return the distance between to vertices
     *
     *
     *
     * @param from
     * @param to
     * @return cost
     */
    public double getCout(int from, int to) {
        return graphe[from][to];
    }

    /**
     *
     * retourne la taille de la population de fourmis
     *
     * @return antPopSize
     */
    public int getAntPopSize() {
        return Parametres.antPopSize;
    }

    /**
     *
     * retourne le voisin le plus pres par position
     *
     * @param from
     * @param index
     * @return targetVertex
     */
    public int getNNNode(int from, int index) {
        return this.NNList[from][index];
    }

    /**
     *
     * retourne la valeur de distance-pheromones a l'intersection entre deux villes
     *
     * @param from
     * @param to
     * @return costInfo
     */
    public double getCostInfo(int from, int to) {
        return choiceInfo[from][to];
    }

    /**
     *
     * retourne le tableau de fourmis
     *
     * @return ants
     */
    public Fourmi[] getAnts() {
        return ants;
    }
}