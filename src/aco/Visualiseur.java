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
    double miseAEchelleL;
    double miseAEchelleH;

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
        largeur = 30;
        hauteur = 30;
        for(int i = 0; i < coordinates.length; i++) {
            if(coordinates[i][0] > miseAEchelleL) {
                miseAEchelleL = (int) coordinates[i][0];
            }
            if(coordinates[i][1] > miseAEchelleH) {
                miseAEchelleH = (int) coordinates[i][1];
            }
        }
        miseAEchelleL = vueLargeur / miseAEchelleL;
        miseAEchelleH = vueHauteur / miseAEchelleH;
        miseAEchelleL *= .9;
        miseAEchelleH *= .9;
    }

    public void draw(int[] tour) {
        this.noeuds.clear();
        this.chemins.clear();
        for(int i = 0; i < coordonnees.length; i++) {
            int x = (int) (coordonnees[i][0] * miseAEchelleL);
            int y = (int) (coordonnees[i][1] * miseAEchelleH);
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


    //Fait l'ajout d'un n??ud au pixel avec les coordonn??es x et y

    public void ajouterChemin(String nom, int x, int y) {
        noeuds.add(new Noeud(nom,x,y));
    }



    //Fait la creation d'une arr??tes entre les n??uds i et j
    public void ajouterNoeud(int i, int j) {
        chemins.add(new Chemin(i,j));
    }



    //Fait une suppression des n??uds et arr??tes repr??senter
    //et on refait l'affichage des n??uds et arr??tes.
    //Surcharge de la m??thode paint dans la classe Window
    @Override
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
