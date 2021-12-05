package aco;

/**
 * TEST1
 */
public class Ant {

    /**
     * Cost of the current path, it is the sum of all edges distances.
     * La variable : ____ représente la somme des coût des toutes les distances entre les noeuds
     */
    private double tourCost;

    /**
     * Current tour, contains a sequence of edges that was travelled by the ant.
     * Start at the first node and ends at the same node, ex: [A,B,C,D,E,A]
     *.
     * La variable _____ représente la séquence des noeuds qui ont été visité par la fourmi.
     * on a donc une séquence accumuler qui débute du peemier jusqu'au dernier ex : [A,B,C,D,E,A]
     *
     */
    private int[] tour;

    /**
     * All nodes that were visited by the ant. Are the restrictions of the TSP problem,
     * where an ant must visit all vertices only one time in a travel.
     *
     * La variable : ______ représente tout les noeuds qui on été visité par la fourmi.
     * Cette varaible booléenne permet de savoir si la fourmi a passer un seul fois par tout les noeuds.
     */
    private boolean[] visited;

    /**
     * Let the information about the edges, vertices and pheromones accessible.
     *
     * Permet de déclarer les information a propos des noeuds, vertèbre et phéromones accessible.
     *
     */
    private Environment environment;

    /**
     * Create an adapted ant to the environment.
     *
     * Permet de faire la création d'une fourmi qui sera adapter à son environnement.
     *
     * @param tourSize
     * @param environment
     */
    public Ant(int tourSize, Environment environment) {
        super();
        this.tour = new int[tourSize + 1];
        this.visited = new boolean[tourSize];
        this.environment = environment;
    }

    /**
     * Calculate the travel cost where in each vertex the ant choose the
     * most near neighbor still not visited starting in a random vertex.
     *
     * Cette méthode nous permet de pouvoir faire le calcul du coût dans lequel la fourmis choisi
     * le voisin le plus proche (noeuds) qui n'a pas encore été visité sur une vertèbre "random"
     * @return cost
     */
    public double calculateNearestNeighborTour() {
        int phase = 0;
        clearVisited();
        startAtRandomPosition(phase);
        while (phase < environment.getNodesSize() - 1) {
            phase++;
            goToBestNext(phase);
        }
        finishTourCircuit();
        clearVisited();
        return this.tourCost;
    }

    /**
     * Reset the ant to be processed.
     *
     * Dans cette méthode, on fait une remise a zéro de la fourmi qui sera utilisée
     */
    public void clearVisited() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
    }

    /**
     * Put the ant in a random vertex and mark it as visited.
     *
     * Dans cette méthode, on place la fourmi sur une vertèbre "random" puis on marque
     * cette vertèbre comme ayant été visité.
     *
     * @param phase
     */
    public void startAtRandomPosition(int phase) {
        tour[phase] = (int) (Math.random() * environment.getNodesSize());
        visited[tour[phase]] = true;
    }

    /**
     * Move to the neighbor of the current vertex with the smaller distance
     *
     * Cette méthode nous permet de pouvoir bouger la fourmi vers la vertèbre voisine qui est la plus courte
     *
     * @param phase
     */
    public void goToBestNext(int phase) {
        // Start considering the last move
        int nextCity = environment.getNodesSize();
        // Take the current city
        int currentCity = tour[phase - 1];
        // Start with a value that an edge never will achieve
        double minDistance = Double.MAX_VALUE;
        // For each non visited vertex, if the cost is lesser than minDistance select it
        for (int city = 0; city < environment.getNodesSize(); city++) {
            if (!visited[city] && environment.getCost(currentCity, city) < minDistance) {
                nextCity = city;
                minDistance = environment.getCost(currentCity, city);
            }
        }
        // Move to the next city
        tour[phase] = nextCity;
        visited[nextCity] = true;
    }

    /**
     * Sum all the (i->i+1) edges cost of the tour. The last vertex (n - 1)
     * reconnect with the first vertex (n), where vertex n is equal to vertex 0.
     *
     * Cette méthode nous permet de faire le calcul total du tour pour la fourmi.
     *
     * @return tourCost
     */
    public double computeTourCost() {
        double tourCost = 0.0;
        for (int i = 0; i < environment.getNodesSize(); i++) {
            tourCost += environment.getCost(tour[i], tour[i + 1]);
        }
        return tourCost;
    }

    /**
     * Finish the ant circuit, bind the last vertex with first vertex (last vertex
     * n is equal to vertex 0) and calculate the route cost.
     *
     *
     */
    public void finishTourCircuit() {
        tour[environment.getNodesSize()] = tour[0];
        tourCost = computeTourCost();
    }

    /**
     * Calculate next neighbor tending probabilistically selecting the edge
     * with more pheromone and smaller distance. First is tried to find
     * a vector to move in the nearest neighbor list, but if all vectors were visited
     * then is selected the best in all remaining neighbors
     *
     * @param phase
     */
    public void goToNNListAsDecisionRule(int phase) {
        // Get the current city

        //Choisir la ville actuelle
        int currentCity = this.tour[phase - 1];
        double sumProbabilities = 0.0;

        // Vector of nearest neighbor probabilities proportional to the fitness of the edge

        // Représente le vecteur de la ville la plus près en proportion par rapport a notre fitness

        double[] selectionProbabilities = new double[environment.getNNSize() + 1];
        // For each nearest neighbor vertex that was not visited yet add their fitness to the
        // probability vector

        //Pour chaque vertèbre voisine qui n'a pas été visitée encore, on ajoute le fitness au
        // vecteur de probabilité

        for(int j = 0; j < environment.getNNSize(); j++) {
            if(visited[environment.getNNNode(currentCity, j)]) {
                selectionProbabilities[j] = 0.0;
            } else {
                selectionProbabilities[j] = environment.getCostInfo(currentCity, environment.getNNNode(currentCity, j));
                sumProbabilities += selectionProbabilities[j];
            }
        }
        if(sumProbabilities <= 0) {
            // If all nearest neighbor were visited select on best in the remaining neighbors

            //si tout les voisins on été visités, on choisi le meilleur voisin qui reste
            goToBestNext(phase);
        } else {
            // Take a random value proportional to the sum of probabilities

            //Prendre une valeur aléatoire qui est proportionnelle a la somme des probabilité
            double rand = Math.random() * sumProbabilities;
            int j = 0;
            double probability = selectionProbabilities[j];

            // Selected the neighbor correspondent to the random proportional probability

            //le voisin avec la probabilité proportionnelle aléatoire est sélectionner.
            while(probability <= rand) {
                j++;
                probability += selectionProbabilities[j];
            }
            // If has problem with double round occurred

            //
            if(j == environment.getNNSize()) {
                // Select the best neighbor
                goToBestNeighbor(phase);
                return;
            }
            // Visit the selected neighbor

            //Permet de faire la visite du noeud qui a été sélectionné.
            tour[phase] = environment.getNNNode(currentCity, j);
            visited[this.tour[phase]] = true;
        }
    }

    /**
     * Select the best non visited neighbor of the current vertex (the best neighbor
     * have the greater fitness calculated from pheromone and heuristic).
     *
     * On fait la sélection du meilleur noeud  qui n'a pas encore été visité du noeud actuel.
     * @param phase
     */
    public void goToBestNeighbor(int phase) {
        int helpCity;
        int nextCity = environment.getNodesSize();
        // Take the current city
        // Prend la ville actuel
        int currentCity = this.tour[phase - 1];
        // Start the best with a value that never will be achieved

        //On débute le meilleur avec un valeur qui ne sera jamais atteinte
        double valueBest = -1.0;
        double help;
        // Select the non visited neighbor with the maximum fitness

        //On sélectionne un voisin non visité qui contient à le fitness maximum
        for(int i = 0; i < environment.getNNSize(); i++) {
            helpCity = environment.getNNNode(currentCity, i);
            if(!this.visited[helpCity]) {
                help = environment.getCostInfo(currentCity, helpCity);
                if(help > valueBest) {
                    valueBest = help;
                    nextCity = helpCity;
                }
            }
        }
        if(nextCity == environment.getNodesSize()) {
            // If was not found a vertex at the nearest neighbor list of the current vector
            // Si aucun noeud n'a été trouvé, on passe au prochain
            goToBestNext(phase);
        } else {
            // Move to the vertex
            tour[phase] = nextCity;
            visited[this.tour[phase]] = true;
        }
    }

    /**
     * Return the cost of the current tour
     *
     * Fait le retour du coût du tour actuel
     *
     * @return tourCost
     */
    public double getTourCost() {
        return tourCost;
    }

    /**
     * Return the vertex for a specific phase of the travel
     *
     * Fait le retour d'une vertèbre pour une phase spécifique du tour
     *
     * @param phase
     * @return vertex
     */
    public int getRoutePhase(int phase) {
        return tour[phase];
    }

    /**
     * Return the current tour
     *
     * Fait le retour du tour actuel
     *
     * @return tour
     */
    public int[] getTour() {
        return tour;
    }

}
