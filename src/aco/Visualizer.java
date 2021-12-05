package aco;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Thanks: http://www1.cs.columbia.edu/~bert/courses/3137/hw3_files/GraphDraw.java
 */
public class Visualizer extends JFrame {

    int VueLargeur;
    int VueHauteur;
    int Largeur;
    int Hauteur;
    double MiseEchelleL;
    double MiseEchelleH;

    ArrayList<Node> noeud;
    ArrayList<edge> edges;

    double[][] coordinates;

    private JLabel stats;

    public Visualizer(double[][] coordinates) {
        super();
        this.coordinates = coordinates;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.stats = new JLabel();
        this.pack();
        this.getContentPane().setBackground(Color.cyan);
        this.setLocationRelativeTo(null);
        this.add(stats, BorderLayout.SOUTH);
        this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        try { Thread.sleep(1000); } catch (Exception ex) {}
        noeud = new ArrayList<Node>();
        edges = new ArrayList<edge>();
        VueLargeur = this.getWidth();
        VueHauteur = this.getHeight();
        Largeur = 60;
        Hauteur = 60;
        for(int i = 0; i < coordinates.length; i++) {
            if(coordinates[i][0] > MiseEchelleL) MiseEchelleL = (int) coordinates[i][0];
            if(coordinates[i][1] > MiseEchelleH) MiseEchelleH = (int) coordinates[i][1];
        }
        MiseEchelleL = VueLargeur / MiseEchelleL;
        MiseEchelleH = VueHauteur / MiseEchelleH;
        MiseEchelleL *= .9;
        MiseEchelleH *= .9;
    }

    public void draw(int[] tour) {
        this.noeud.clear();
        this.edges.clear();
        for(int i = 0; i < coordinates.length; i++) {
            int x = (int) (coordinates[i][0] * MiseEchelleL);
            int y = (int) (coordinates[i][1] * MiseEchelleH);
            this.addNode(String.valueOf(i), x, y);
        }
        for(int i = 0; i < tour.length - 1; i++) {
            this.addEdge(tour[i], tour[i + 1]);
        }
        this.repaint();
    }

    public void setStat(String text) {
        this.stats.setText(text);
    }

    class Node {
        int x, y;
        String name;

        public Node(String myName, int myX, int myY) {
            x = myX;
            y = myY;
            name = myName;
        }
    }

    class edge {
        int i,j;

        public edge(int ii, int jj) {
            i = ii;
            j = jj;
        }
    }

    // Add a node at pixel (x,y)

    //fait l'ajout d'un noeud au pixel avec les coordonnées x et y

    public void addNode(String name, int x, int y) {
        noeud.add(new Node(name,x,y));
    }

    // Add an edge between nodes i and j

    //fait la cration d'une arrêtes entre les noeud i et j
    public void addEdge(int i, int j) {
        edges.add(new edge(i,j));
    }

    // Clear and repaint the nodes and edges

    //Fait une suppression des noeuds et arrête représenter
    // et on refait les l'affichage des noeuds et arrêtes
    public void paint(Graphics g) {
        super.paint(g);
        FontMetrics f = g.getFontMetrics();
        int nodeHeight = Math.max(Hauteur, f.getHeight());
        g.setColor(Color.black);
        for (edge e : edges) {
            g.drawLine(noeud.get(e.i).x, noeud.get(e.i).y, noeud.get(e.j).x, noeud.get(e.j).y);
        }
        for (Node n : noeud) {
            int nodeWidth = Math.max(Largeur, f.stringWidth(n.name)+ Largeur /2);
            g.setColor(Color.white);
            g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2, nodeWidth, nodeHeight);
            g.setColor(Color.black);
            g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2, nodeWidth, nodeHeight);
            g.drawString(n.name, n.x-f.stringWidth(n.name)/10,n.y+f.getHeight()/2);
        }
    }
}
