package aco;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Thanks: http://www1.cs.columbia.edu/~bert/courses/3137/hw3_files/GraphDraw.java
 */
public class Visualiseur extends JFrame {

    int vueLargeur;
    int vueHauteur;
    int largeur;
    int hauteur;
    double miseEchelleL;
    double miseEchelleH;

    ArrayList<Noeud> noeuds;
    ArrayList<Chemin> chemins;

    double[][] coordonnees;

    private JLabel statistiques;

    public Visualiseur(double[][] coordinates) {
        super();
        this.coordonnees = coordinates;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.statistiques = new JLabel();
        this.pack();
        this.getContentPane().setBackground(Color.cyan);
        this.setLocationRelativeTo(null);
        this.add(statistiques, BorderLayout.SOUTH);
        this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        try { Thread.sleep(1000); } catch (Exception ex) {}
        noeuds = new ArrayList<Noeud>();
        chemins = new ArrayList<Chemin>();
        vueLargeur = this.getWidth();
        vueHauteur = this.getHeight();
        largeur = 60;
        hauteur = 60;
        for(int i = 0; i < coordinates.length; i++) {
            if(coordinates[i][0] > miseEchelleL) miseEchelleL = (int) coordinates[i][0];
            if(coordinates[i][1] > miseEchelleH) miseEchelleH = (int) coordinates[i][1];
        }
        miseEchelleL = vueLargeur / miseEchelleL;
        miseEchelleH = vueHauteur / miseEchelleH;
        miseEchelleL *= .9;
        miseEchelleH *= .9;
    }

    public void draw(int[] tour) {
        this.noeuds.clear();
        this.chemins.clear();
        for(int i = 0; i < coordonnees.length; i++) {
            int x = (int) (coordonnees[i][0] * miseEchelleL);
            int y = (int) (coordonnees[i][1] * miseEchelleH);
            this.ajouterChemin(String.valueOf(i), x, y);
        }
        for(int i = 0; i < tour.length - 1; i++) {
            this.ajouterNoeud(tour[i], tour[i + 1]);
        }
        this.repaint();
    }

    public void setStat(String text) {
        this.statistiques.setText(text);
    }

    class Noeud {
        int x, y;
        String name;

        public Noeud(String myName, int myX, int myY) {
            x = myX;
            y = myY;
            name = myName;
        }
    }

    class Chemin {
        int i,j;

        public Chemin(int ii, int jj) {
            i = ii;
            j = jj;
        }
    }

    // Add a node at pixel (x,y)

    //fait l'ajout d'un noeud au pixel avec les coordonnées x et y

    public void ajouterChemin(String nom, int x, int y) {
        noeuds.add(new Noeud(nom,x,y));
    }

    // Add an edge between nodes i and j

    //fait la cration d'une arrêtes entre les noeud i et j
    public void ajouterNoeud(int i, int j) {
        chemins.add(new Chemin(i,j));
    }

    // Clear and repaint the nodes and edges

    //Fait une suppression des noeuds et arrêtes représenter
    //et on refait l'affichage des noeuds et arrêtes
    //Surcharge de la méthode paint dans la classe Window
    public void paint(Graphics g) {
        super.paint(g);
        FontMetrics f = g.getFontMetrics();
        int hauteurDuNoeud = Math.max(hauteur, f.getHeight());
        g.setColor(Color.black);
        for (Chemin e : chemins) {
            g.drawLine(noeuds.get(e.i).x, noeuds.get(e.i).y, noeuds.get(e.j).x, noeuds.get(e.j).y);
        }
        for (Noeud n : noeuds) {
            int nodeWidth = Math.max(largeur, f.stringWidth(n.name)+ largeur /2);
            g.setColor(Color.white);
            g.fillOval(n.x-nodeWidth/2, n.y-hauteurDuNoeud/2, nodeWidth, hauteurDuNoeud);
            g.setColor(Color.black);
            g.drawOval(n.x-nodeWidth/2, n.y-hauteurDuNoeud/2, nodeWidth, hauteurDuNoeud);
            g.drawString(n.name, n.x-f.stringWidth(n.name)/10,n.y+f.getHeight()/2);
        }
    }
}
