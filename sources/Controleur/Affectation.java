package Controleur;

import java.awt.*;

public class Affectation {
    Point positionCaisse;
    Point positionCible;
    int distance;

    public Affectation(Point positionCaisse, Point positionCible, int distance) {
        this.positionCaisse = positionCaisse;
        this.positionCible = positionCible;
        this.distance = distance;
    }
}
