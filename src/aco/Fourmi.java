package aco;

public class Fourmi {

    /**
     * La variable représente la somme des coûts de toutes les distances entre les nœuds
     */
    private double distanceTotal;

    /**
     * La variable _____ représente la séquence des nœuds qui ont été visité par la fourmi.
     * on a donc une séquence accumuler qui débute du premier jusqu'au dernier ex : [A,B,C,D,E]
     *
     */
    private int[] sequenceNoeuds;

    /**
     * La variable  représente tous les nœuds qui ont été visité par la fourmi.
     * Cette variable booléenne permet de savoir si la fourmi a passé une seule fois par tous les nœuds.
     */
    private boolean[] noeudsVisite;

    /**
     * Permet de déclarer les informations à propos des nœuds, vertèbre et phéromones accessible.
     *
     */
    private Environnement environnement;

    /**
     *
     * Permet de faire la création d'une fourmi qui sera adapter à son environnement.
     *  @param nombreDeNoeuds
     * @param environnement
     */
    public Fourmi(int nombreDeNoeuds, Environnement environnement) {
        super();
        this.sequenceNoeuds = new int[nombreDeNoeuds + 1];
        this.noeudsVisite = new boolean[nombreDeNoeuds];
        this.environnement = environnement;
    }

    /**
     * Cette méthode nous permet de pouvoir faire le calcul du coût dans lequel la fourmi choisi
     * le voisin le plus proche (nœuds) qui n'a pas encore été visité sur une vertèbre "random"
     * @return this.distanceTotal
     */
    public double calculerVoisinPlusProche() {
        int phase = 0;
        nettoyerVisiter();
        debuterAUnePositionAleatoire(phase);
        while (phase < environnement.getNombreDeNoeuds() - 1) {
            phase++;
            allerAuProchainMeilleurNoeud(phase);
        }
        terminerSequence();
        nettoyerVisiter();
        return this.distanceTotal;
    }

    /**
     *
     * Dans cette méthode, on fait une remise a zéro de la fourmi qui sera utilisée
     */
    public void nettoyerVisiter() {
        for (int i = 0; i < noeudsVisite.length; i++) {
            noeudsVisite[i] = false;
        }
    }

    /**
     * Dans cette méthode, on place la fourmi sur une vertèbre "random" puis on marque
     * cette vertèbre comme ayant été visité.
     *
     * @param phase
     */
    public void debuterAUnePositionAleatoire(int phase) {
        sequenceNoeuds[phase] = (int) (Math.random() * environnement.getNombreDeNoeuds());
        noeudsVisite[sequenceNoeuds[phase]] = true;
    }

    /**
     * Cette méthode nous permet de pouvoir bouger la fourmi vers la vertèbre voisine qui est la plus courte
     *
     * @param phase
     */
    public void allerAuProchainMeilleurNoeud(int phase) {

        //Commencez à envisager le prochain déplacement
        int prochaineVille = environnement.getNombreDeNoeuds();
        //Sélectionne la ville actuelle
        int villeActuelle = sequenceNoeuds[phase - 1];
        //Commencez avec une valeur qui ne sera pas atteignable
        double distanceMin = Double.MAX_VALUE;

        //Pour chaque sommet non visité, si le coût est inférieur à distanceMin, sélectionnez-le
        for (int Ville = 0; Ville < environnement.getNombreDeNoeuds(); Ville++) {
            if (!noeudsVisite[Ville] && environnement.getCout(villeActuelle, Ville) < distanceMin) {
                prochaineVille = Ville;
                distanceMin = environnement.getCout(villeActuelle, Ville);
            }
        }
        //Déplacement à la prochaine ville
        sequenceNoeuds[phase] = prochaineVille;
        noeudsVisite[prochaineVille] = true;
    }

    /**
     * Cette méthode nous permet de faire le calcul total du tour pour la fourmi.
     *
     * @return distanceTotal
     */
    public double calculerCoutSequence() {
        double distanceTotal = 0.0;
        for (int i = 0; i < environnement.getNombreDeNoeuds(); i++) {
            distanceTotal += environnement.getCout(sequenceNoeuds[i], sequenceNoeuds[i + 1]);
        }
        return distanceTotal;
    }

    /**
     *
     *Terminez le circuit des fourmis, liez le dernier nœud avec le premier nœud (dernier nœud
     *n est égal au nœud 0) et calculez le coût de l'itinéraire.
     */
    public void terminerSequence() {
        sequenceNoeuds[environnement.getNombreDeNoeuds()] = sequenceNoeuds[0];
        distanceTotal = calculerCoutSequence();
    }

    /**
     *
     * Calculer la tendance du prochain voisin en sélectionnant probabiliste le chemin
     * avec plus de phéromone et une distance plus petite. On essaie d'abord de trouver
     * un vecteur pour se déplacer dans la liste des voisins les plus proches, mais si tous les vecteurs ont été visités
     * alors on sélectionne le meilleur parmi tous les voisins restants
     *
     * @param phase
     */
    public void goToNNListAsDecisionRule(int phase) {

        //Choisir la ville actuelle
        int villeActuelle = this.sequenceNoeuds[phase - 1];
        double sommeDesProbabilities = 0.0;

        // Représente le vecteur de la ville la plus près en proportion par rapport à notre fitness

        double[] selectionDeProbabilitie = new double[environnement.getNNSize() + 1];

        //Pour chaque vertèbre voisine qui n'a pas été visitée encore, on ajoute le fitness au
        //vecteur de probabilité

        for(int j = 0; j < environnement.getNNSize(); j++) {
            if(noeudsVisite[environnement.getNNNode(villeActuelle, j)]) {
                selectionDeProbabilitie[j] = 0.0;
            } else {
                selectionDeProbabilitie[j] = environnement.getCostInfo(villeActuelle, environnement.getNNNode(villeActuelle, j));
                sommeDesProbabilities += selectionDeProbabilitie[j];
            }
        }
        if(sommeDesProbabilities <= 0) {

            //Si tous les voisins ont été visités, le meilleur voisin qui reste est choisi
            allerAuProchainMeilleurNoeud(phase);
        } else {

            //Prendre une valeur aléatoire qui est proportionnelle a la somme des probabilités
            double rand = Math.random() * sommeDesProbabilities;
            int j = 0;
            double probabilite = selectionDeProbabilitie[j];

            //le voisin avec la probabilité proportionnelle aléatoire est sélectionné.
            while(probabilite <= rand) {
                j++;
                probabilite += selectionDeProbabilitie[j];
            }

            //Si le problème d'arrondissement se produit (Java)
            if(j == environnement.getNNSize()) {
                //Choisi le meilleur voisin
                goToBestNeighbor(phase);
                return;
            }

            //Permet de faire la visite du nœud qui a été sélectionné.
            sequenceNoeuds[phase] = environnement.getNNNode(villeActuelle, j);
            noeudsVisite[this.sequenceNoeuds[phase]] = true;
        }
    }

    /**
     * On fait la sélection du meilleur nœud qui n'a pas encore été visité du nœud actuel.
     * @param phase
     */
    public void goToBestNeighbor(int phase) {
        int villeTemporaire;
        int prochaineVille = environnement.getNombreDeNoeuds();

        //Sélectionne la ville actuelle
        int villeActuelle = this.sequenceNoeuds[phase - 1];

        //On débute le meilleur avec une valeur qui ne sera jamais atteinte
        double meilleurCoutADate = -1.0;
        double coutDeVilleTemporaire;

        //On sélectionne un voisin non visité qui contient le fitness maximum
        for(int i = 0; i < environnement.getNNSize(); i++) {
            villeTemporaire = environnement.getNNNode(villeActuelle, i);
            if(!this.noeudsVisite[villeTemporaire]) {
                coutDeVilleTemporaire = environnement.getCostInfo(villeActuelle, villeTemporaire);
                if(coutDeVilleTemporaire > meilleurCoutADate) {
                    meilleurCoutADate = coutDeVilleTemporaire;
                    prochaineVille = villeTemporaire;
                }
            }
        }
        if(prochaineVille == environnement.getNombreDeNoeuds()) {

            //Si aucun nœud n'a été trouvé, on passe au prochain
            allerAuProchainMeilleurNoeud(phase);
        } else {
            //Déplacement vers la prochaine ville
            sequenceNoeuds[phase] = prochaineVille;
            noeudsVisite[this.sequenceNoeuds[phase]] = true;
        }
    }

    /**
     * Fait le retour du coût du tour actuel
     *
     * @return distanceTotal
     */
    public double getDistanceTotal() {
        return distanceTotal;
    }

    /**
     * Fait le retour d'une vertèbre pour une phase spécifique du tour
     *
     * @param phase
     * @return sequenceNoeuds[phase]
     */
    public int getPhaseSequence(int phase) {
        return sequenceNoeuds[phase];
    }

    /**
     * Fait le retour du tour actuel
     *
     * @return sequenceNoeuds
     */
    public int[] getSequenceNoeuds() {
        return sequenceNoeuds;
    }
}