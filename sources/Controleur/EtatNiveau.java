package Controleur;

import Global.Configuration;
import Modele.Niveau;

import java.util.ArrayList;
import java.awt.Point;

public class EtatNiveau {

    public Point positionDuPousseur;
    public ArrayList<Point> positionsDesCaisses;

    // deplacements
    final static int HAUT = 0;
    final static int GAUCHE = 1;
    final static int BAS = 2;
    final static int DROITE = 3;

    public EtatNiveau(Point positionPousseur, ArrayList<Point> positionsCaisses) {
        positionDuPousseur = positionPousseur;
        positionsDesCaisses = positionsCaisses;
    }

    public EtatNiveau(Niveau niveau) {

        positionDuPousseur = new Point(niveau.colonnePousseur(), niveau.lignePousseur());
        positionsDesCaisses = new ArrayList<Point>();

        int colonne = 0;
        int lignes;
        while (colonne < niveau.colonnes()) {
            lignes = 0;
            while (lignes < niveau.lignes()) {
                if (niveau.aCaisse(lignes, colonne)) {
                    positionsDesCaisses.add(new Point(colonne, lignes));
                }
                lignes = lignes + 1;
            }
            colonne = colonne + 1;
        }
    }

    @Override
    public EtatNiveau clone() {
        return new EtatNiveau(positionDuPousseur, positionsDesCaisses);
    }

    @Override
    public boolean equals(Object obj) {
        boolean deplacementPossible = false;
        if (obj == null) {
            deplacementPossible = false;
        } else {
            if (obj.getClass() != this.getClass()) {
                deplacementPossible = false;
            } else {
                EtatNiveau objetCompare = (EtatNiveau) obj;
                if (positionDuPousseur.equals(objetCompare.positionDuPousseur)
                        && positionsDesCaisses.equals(objetCompare.positionsDesCaisses)) {
                    deplacementPossible = true;
                }
            }
        }
        return deplacementPossible;
    }

    @Override
    public int hashCode() {
        return positionDuPousseur.hashCode() * positionsDesCaisses.hashCode();
    }

    private Point deplacement(Point deplacementElement, int sensDeplacement) {

        // Origine de repère en (0,0)
        Point deplacement = new Point(0, 0);
        switch (sensDeplacement) {
            case HAUT:
                deplacement.x = deplacementElement.x;
                deplacement.y = deplacementElement.y - 1;
                break;
            case GAUCHE:
                deplacement.x = deplacementElement.x - 1;
                deplacement.y = deplacementElement.y;
                break;
            case BAS:
                deplacement.x = deplacementElement.x;
                deplacement.y = deplacementElement.y + 1;
                break;
            case DROITE:
                deplacement.x = deplacementElement.x + 1;
                deplacement.y = deplacementElement.y;
                break;
            default:
                Configuration.instance().logger().warning("Deplacement inconnu !");
                break;
        }
        return deplacement;
    }

    public boolean deplacementPossible(int sensDeplacement, Niveau niveau) {
        boolean deplacementPossible = false;

        Point pousseurApresDeplacement = deplacement(positionDuPousseur, sensDeplacement);
        Point caissesApresDeplacement;

        if (!niveau.aMur(pousseurApresDeplacement.y, pousseurApresDeplacement.x) && !aCaisses(pousseurApresDeplacement.x, pousseurApresDeplacement.y)) {
            deplacementPossible = true;
        } else {
            if (aCaisses(pousseurApresDeplacement.x, pousseurApresDeplacement.y)) {
                caissesApresDeplacement = deplacement(pousseurApresDeplacement, sensDeplacement);

                if (!niveau.aMur(caissesApresDeplacement.y, caissesApresDeplacement.x) && !aCaisses(caissesApresDeplacement.x, caissesApresDeplacement.y)) {
                    deplacementPossible = true;
                }
            }
        }
        return deplacementPossible;
    }

    public boolean aCaisses(int colonne, int ligne) {
        boolean deplacementPossible = false;
        int i = 0;
        while (!deplacementPossible && i < positionsDesCaisses.size()) {
            if (positionsDesCaisses.get(i).x == colonne && positionsDesCaisses.get(i).y == ligne) {
                deplacementPossible = true;
            } else {
                i = i + 1;
            }
        }
        return deplacementPossible;
    }

    public EtatNiveau etatNiveauApresDeplacement(int sensDeplacement) {
        
        EtatNiveau etatNiveauApresDeplacement;
        Point pousseurApresDeplacement = deplacement(positionDuPousseur, sensDeplacement);
        Point caissesApresDeplacement;

        etatNiveauApresDeplacement = new EtatNiveau(pousseurApresDeplacement, new ArrayList<Point>());

        int i = 0;
        // Parcours des caisses
        while (i < positionsDesCaisses.size()) {
            // Pousseru déplace une caisse
            if (pousseurApresDeplacement.x == positionsDesCaisses.get(i).x && pousseurApresDeplacement.y == positionsDesCaisses.get(i).y) {
                caissesApresDeplacement = deplacement(positionsDesCaisses.get(i), sensDeplacement);
            } else {
                caissesApresDeplacement = (Point) positionsDesCaisses.get(i).clone();
            }
            etatNiveauApresDeplacement.positionsDesCaisses.add(caissesApresDeplacement);
            i = i + 1;
        }
        return etatNiveauApresDeplacement;
    }
}