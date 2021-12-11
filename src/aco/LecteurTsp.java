package aco;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Référence : https://github.com/phil8192/tsp-java
 * Répertoire de classe : https://github.com/phil8192/tsp-java/blob/master/src/main/java/net/parasec/tsp/TSPReader.java
 */
public class LecteurTsp {


    static double[] tabCoordonnees;

    static int compteur = 0;

    static double[][] donnees;
    static double[][] distances;
    static String typeDePoidsChemin;

    static boolean estDonnees = false;
    static double xd, yd, rij, tij;


    public static double[][] getDistances(String cheminFichier, String fichier) {
        return LecteurTsp.getDistances(new File(cheminFichier, fichier).toString());
    }

    public static double[][] getCoordonnees(String cheminFichier, String fichier) {
        return LecteurTsp.getCoordonnees(new File(cheminFichier, fichier).toString());
    }

    public static double[][] getCoordonnees(String fichier) {
        estDonnees = false;
        compteur = 0;
        extractionCoordonnees(fichier);
        System.out.println();
        return donnees;
    }

    public static double[][] getDistances(String file) {
        estDonnees = false;
        compteur = 0;
        extractionCoordonnees(file);
        calculerDistance();
        return distances;
    }

    public static String getTypeDePoidsChemin(String fichier) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fichier));
            String input = null;
            try {
                while ((input = in.readLine()) != null) {
                    if (input.contains("EDGE_WEIGHT_TYPE")) {
                        Pattern p = Pattern.compile("EDGE_WEIGHT_TYPE : (.+)");
                        Matcher m = p.matcher(input);
                        if (m.matches()) {
                            return m.group(1);
                        } else {
                            return null;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Lecture du fichier
    public static void lectureDuFichier(String fichier) {
        estDonnees = false;
        compteur = 0;
        if (fichier.length() > 0) {
            extractionCoordonnees(fichier);
            calculerDistance();
            afficher();
        } else {
            System.out.print("Entrer le dossier comme argument: ");
            Scanner scan = new Scanner(System.in);
            extractionCoordonnees(scan.nextLine());
            calculerDistance();
            afficher();
        }
        System.exit(0);
    }

    //Afficher la distance de la matrice
    public static void afficher() {
        System.out.println("\nDistance de la matrice correspondante");
        StringBuilder dataString = new StringBuilder();
        dataString.append("\t");
        for (int i = 0; i < distances.length; i++) {
            dataString.append((i) + ":\t");
            for (int j = 0; j < distances.length; j++) {
                dataString.append(distances[i][j] + "\t");
                if (j == distances.length - 1) {
                    dataString.append("\n\t");
                }
            }
        }
        System.out.println("\n" + dataString.toString());
    }

//Calculer la distance de chaque ville
    private static void calculerDistance() {
        if (typeDePoidsChemin.equals("ATT")) {
            for (int i = 0; i < donnees.length; i++) {
                for (int j = 0; j < donnees.length; j++) {
                    if (i == j) {
                        distances[i][j] = 0;
                    } else {
                        xd = donnees[i][0] - donnees[j][0];
                        yd = donnees[i][1] - donnees[j][1];
                        rij = (float) Math.sqrt(((xd * xd) + (yd * yd)) / 10.0);
                        tij = Math.round(rij);
                        if (tij < rij) {
                            distances[i][j] = tij + 1;
                        } else {
                            distances[i][j] = tij;
                        }
                    }
                }
            }
        } else if (typeDePoidsChemin.equals("EUC_2D")) {
            for (int i = 0; i < donnees.length; i++) {
                for (int j = 0; j < donnees.length; j++) {
                    if (i == j) {
                        distances[i][j] = 0;
                    } else {
                        xd = donnees[i][0] - donnees[j][0];
                        yd = donnees[i][1] - donnees[j][1];
                        distances[i][j] = Math.round(Math.sqrt((xd * xd) + (yd * yd)));
                    }
                }
            }
        }
    }

    private static void extractionCoordonnees(String fichier) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fichier));
            String input = null;
            try {
                while ((input = in.readLine()) != null) {
                    input = input.trim();
                    input = input.replace("   ", " ");
                    input = input.replace("  ", " ");
                    if (input.contains("DIMENSION") && !estDonnees) {
                        Pattern p = Pattern.compile("(\\d+)");
                        Matcher m = p.matcher(input);
                        if (m.find()) {
                            distances = new double[Integer.parseInt(m.group(0))][Integer.parseInt(m.group(0))];
                            donnees = new double[Integer.parseInt(m.group(0))][2];
                        }
                    }
                    if (input.contains("EDGE_WEIGHT_TYPE") && !estDonnees) {
                        Pattern p = Pattern.compile("EDGE_WEIGHT_TYPE : (.+)");
                        Matcher m = p.matcher(input);
                        if (m.matches()) {
                            typeDePoidsChemin = m.group(1);
                        }
                    }
                    if (input.contains("NODE_COORD_SECTION") && !estDonnees) {
                        estDonnees = true;
                    } else if (estDonnees) {
                        if (!input.equals("EOF")) {
                            String[] coordinates = input.split(" ");
                            tabCoordonnees = new double[2];
                            tabCoordonnees[0] = Double.parseDouble(coordinates[1]);
                            tabCoordonnees[1] = Double.parseDouble(coordinates[2]);
                            donnees[compteur++] = tabCoordonnees;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
