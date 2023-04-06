package Controleur;

import java.awt.*;
import java.util.ArrayList;

public class EtatPondere {
    EtatNiveauBis etatNiveau;
    int poids;

    public EtatPondere(EtatNiveauBis etat) {
        etatNiveau = etat;
        poids = -1;
    }

    public int CalculDistances() {
        int distance = 0;
        ArrayList<Point> cibles = etatNiveau.positionsDesCibles;
        Affectation affectation;
        // Parcourt de toutes les caisses en trouvant laquelle est la plus proche de son but
        for (int i = 0; i < etatNiveau.positionsDesCaisses.size(); i++) {
            affectation = plusProche(cibles, etatNiveau.positionsDesCaisses.get(i));
            distance += distanceManhattan(etatNiveau.positionsDesCaisses.get(i), affectation.positionCible);
            cibles.remove(affectation.positionCible);
        }
        return distance;
    }

    // Fonction qui renvoie la cible la plus proche d'une caisse
    public Affectation plusProche(ArrayList<Point> cibles, Point caisse) {
        Point but = new Point();
        int min = Integer.MAX_VALUE;
        // Parcourt de tous les buts en trouvant lequel est le plus proche de la caisse
        for (int i = 0; i < cibles.size(); i++) {
            if (cibles.get(i).distance(caisse) < min) {
                int distance = distanceManhattan(caisse, cibles.get(i));
                if (distance < min)
                    min = distance;
                but = cibles.get(i);
            }
        }
        return new Affectation(caisse, but, min);
    }

    public int distanceManhattan(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}