package aco;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Représente l'espace de solution, les nœuds et les arcs du graphe à
 * optimiser et la quantité de pheromones laissé par les fourmis
 */
public class Environnement {

    /**
     * Valeur pour la quantité initiale de pheromones
     */
    private double cheminInitiale;

    /**
     * La matrice du problème du graphe à résoudre de dimension NxN
     */
    private double[][] graphe;

    /**
     * La matrice de NxN voisin le plus proche pour chaque sommet du graphe
     */
    private int[][] listeDesVoisinsLesPlusProches;

    /**
     * Indication de la quantité de pheromones laissées par les fourmis
     */
    private double[][] pheromone;

    /**
     *La matrice NxN stockant la valeur des meilleurs chemins calculées en utilisant
     *la quantité de phéromone et la qualité des bords (plus petites distances).
     */
    private double[][] infoChoixChemin;
    
    /**
     * Les agents fourmis contenus dans l'environnement
     */
    private Fourmi[] fourmis;

    /**
     * Le graphe à résoudre dans l'environnement
     *
     * @param graphe
     */
    public Environnement(double[][] graphe) {
        super();
        this.graphe = graphe;
    }

    /**
     * Créer une liste des voisins les plus proches nn dans une structure de dimension n x nn
     * ou n est la taille de la population et nn la taille des voisins les plus pres
     * listeDesVoisinsLesPlusProches[nombreDeNoeuds][nombresDeVoisinsLesPlusProches]
     */
    public void genererListeDesVoisinsLesPlusProches() {
        listeDesVoisinsLesPlusProches = new int[getNombreDeNoeuds()][getNombresDeVoisinsLesPlusProches()];

        //Pour chaque nœud dans le graph, on classe les voisins par distance
        //on classe la liste par la taille de nn
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            Integer[] indexNoeud = new Integer[getNombreDeNoeuds()];
            Double[] infoNoeud = new Double[getNombreDeNoeuds()];
            for(int j = 0; j < getNombreDeNoeuds(); j++) {
                indexNoeud[j] = j;
                infoNoeud[j] = getCout(i, j);
            }

            // Le chemin courant avec lui-même est laissé comme dernière
            // option à sélectionner dans la liste des voisins les plus proches
            
            infoNoeud[i] = Collections.max(Arrays.asList(infoNoeud));
            Arrays.sort(indexNoeud, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(infoNoeud[o1], infoNoeud[o2]);
                }
            });
            for(int r = 0; r < getNombresDeVoisinsLesPlusProches(); r++) {
                listeDesVoisinsLesPlusProches[i][r] = indexNoeud[r];
            }
        }
    }

    /**
     * Créer une population avec k fourmis pour chercher les solutions à l'environnement
     */
    public void genererPopulationFourmis() {
        fourmis = new Fourmi[getTaillePopulationFourmis()];
        for(int k = 0; k < getTaillePopulationFourmis(); k++) {
            fourmis[k] = new Fourmi(getNombreDeNoeuds(), this);
        }
    }

    /**
     *  Création d'une structure d'informations sur les phéromones et les choix de chemins. La phéromone
     *  est utilisée pour représenter la qualité des chemins utilisés pour construire des solutions.
     *  infoChoixChemin est calculé avec la phéromone et la qualité des chemins, a utilisé par les
     *  fourmis comme règle de décision et index pour accélérer l'algorithme.
     *
     *  Pour générer l'environnement la phéromone est initialisée en tenant compte du coût
     *  du tour du voisin le plus proche.
     */

    public void genererEnvironnement() {
        pheromone = new double[getNombreDeNoeuds()][getNombreDeNoeuds()];
        infoChoixChemin = new double[getNombreDeNoeuds()][getNombreDeNoeuds()];
        cheminInitiale = 1.0 / (Parametres.tauxEvaporationPheromones * fourmis[0].calculerVoisinPlusProche());
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = i; j < getNombreDeNoeuds(); j++) {
                pheromone[i][j] = cheminInitiale;
                pheromone[j][i] = cheminInitiale;
                infoChoixChemin[i][j] = cheminInitiale;
                infoChoixChemin[j][i] = cheminInitiale;
            }
        }
        calculerLesInfosDuChoixDuChemin();
    }


    /**
     * Calculer la probabilité proportionnelle d'une fourmi au nœud i de sélectionner un
     * voisin j basé sur le coût (l'inverse du coût) du chemin (i → j) ainsi que
     * la quantité de phéromone sur le chemin (i → j). Les paramètres importanceDesPheromones
     * et importanceDeHeuristique contrôlent l'équilibre entre l'heuristique et la phéromone.
     */
    public void calculerLesInfosDuChoixDuChemin() {
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCout(i, j) + 0.1));
                infoChoixChemin[i][j] = Math.pow(pheromone[i][j], Parametres.importanceDesPheromones) * Math.pow(heuristic, Parametres.importanceDeHeuristique);
                infoChoixChemin[j][i] = infoChoixChemin[i][j];
            }
        }
    }

    /**
     * Chaque fourmi va construire une solution dans l'environnement
     */
    public void construireSolutions() {
        //À la premiere étape, réinitialise toutes les fourmis et mettre
        // chacune d'elle dans un nœud (ville) aléatoire du graphe
        int phase = 0;
        for(int k = 0; k < getTaillePopulationFourmis(); k++) {
            fourmis[k].nettoyerVisiter();
            fourmis[k].debuterAUnePositionAleatoire(phase);
        }
        //Faire en sorte que toutes les fourmis choisissent le prochain nœud non-visiter
        //basée sur la quantité de pheromones et l'heuristique du coût du chemin.
        while(phase < getNombreDeNoeuds() - 1) {
            phase++;
            for(int k = 0; k < getTaillePopulationFourmis(); k++) {
                fourmis[k].allerALaListeDesVoisinsLesPlusProchesCommeRègleDeDecision(phase);
            }
        }
        //Terminer la sequence et calculer le tout total.
        for(int k = 0; k < getTaillePopulationFourmis(); k++) {
            fourmis[k].terminerSequence();
        }
    }

    /**
     * Mettre à jour la phéromone en tenant compte de la qualité de construction
     * des solutions par les fourmis et le taux d'évaporation des pheromones
     */
    public void miseajourPheromone() {
        evaporationPheromones();
        for(int k = 0; k < getTaillePopulationFourmis(); k++) {
            depotPheromones(fourmis[k]);
        }
        calculerLesInfosDuChoixDuChemin();
    }

    /**
     * Évaporer la quantité de phéromone par un facteur exponentiel
     * (1 - tauxEvaporationPheromones) pour tous les chemins
     */
    public void evaporationPheromones() {
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            for(int j = i; j < getNombreDeNoeuds(); j++) {
                pheromone[i][j] = (1 - Parametres.tauxEvaporationPheromones) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }
    /**
     * Déposez la quantité de phéromone dans tous les chemins utilisés par la fourmi ou
     * la quantité de phéromone déposée est proportionnelle à la qualité de la solution.
     *
     * @param fourmi
     */
    public void depotPheromones(Fourmi fourmi) {
        double dTau = 1.0 / fourmi.getDistanceTotal();
        for(int i = 0; i < getNombreDeNoeuds(); i++) {
            int j = fourmi.getSequenceDeLaPhase(i);
            int l = fourmi.getSequenceDeLaPhase(i + 1);
            pheromone[j][l] = pheromone[j][l] + dTau;
            pheromone[l][j] = pheromone[j][l];
        }
    }

    /**
     * Retourne le nombre de nœuds (la population du graphe)
     *
     * @return graphe.length
     */
    public int getNombreDeNoeuds() {
        return graphe.length;
    }

    /**
     *
     * Retourne la quantité de voisins les plus proches
     *
     * @return Parametres.tailleDeLaListeDesVoisinsLesPlusProches
     */
    public int getNombresDeVoisinsLesPlusProches() { return Parametres.tailleDeLaListeDesVoisinsLesPlusProches; }

    /**
     * Retourne le coût (distance) entre les nœuds
     *
     * @param depart
     * @param destination
     * @return cost
     */
    public double getCout(int depart, int destination) {
        return graphe[depart][destination];
    }

    /**
     * Retourne la taille de la population de fourmis
     *
     * @return antPopSize
     */
    public int getTaillePopulationFourmis() {
        return Parametres.taillePopulationFourmis;
    }

    /**
     * Retourne le voisin le plus proche de la position du rang d'index
     *
     * @param depart
     * @param index
     * @return this.listeDesVoisinsLesPlusProches[depart][index]
     */
    public int getVoisinLePlusProche(int depart, int index) {
        return this.listeDesVoisinsLesPlusProches[depart][index];
    }


    /**
     * Renvoie la valeur heuristique-phéromone du chemin entre départ et destination
     *
     * @param depart
     * @param destination
     * @return infoChoixChemin[depart][destination]
     */
    public double getCostInfo(int depart, int destination) {
        return infoChoixChemin[depart][destination];
    }

    /**
     * Retourne le tableau de fourmis (population de fourmis dans l'environnement)
     *
     * @return fourmis
     */
    public Fourmi[] getAnts() {
        return fourmis;
    }
}