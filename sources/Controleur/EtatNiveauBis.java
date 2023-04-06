package Controleur;

import Modele.Niveau;

import java.util.ArrayList;
import java.awt.Point;

public class EtatNiveauBis {

    public ArrayList<Point> positionsDesCaisses;
    public ArrayList<Point> positionsDesCibles;
    Point positionPousseur;
    Niveau niveau;
    boolean estVisite;

    // deplacements
    final static int HAUT = 0;
    final static int DROITE = 1;
    final static int BAS = 2;
    final static int GAUCHE = 3;
    int cout = 0;

    public EtatNiveauBis(Niveau niveau) {
        this.niveau = niveau;
        this.positionPousseur = new Point(niveau.pousseurL, niveau.pousseurC);
        this.positionsDesCaisses = new ArrayList<Point>();
        this.positionsDesCibles = new ArrayList<Point>();
        this.estVisite = false;
        for(int c = 0; c < niveau.colonnes(); c++){
            for(int l = 0; l < niveau.lignes(); l++){
                if(niveau.aCaisse(l, c)){
                    this.positionsDesCaisses.add(new Point(l, c));
                }
                if(niveau.aBut(l, c)){
                    this.positionsDesCibles.add(new Point(l, c));
                }
            }
        }
    }

    public EtatNiveauBis() {
        this.niveau = null;
        this.positionPousseur = null;
        this.positionsDesCaisses = null;
        this.positionsDesCibles = null;
        this.estVisite = false;
    }

    public CoupCaisse deplacement_possible(int direction){
        switch(direction){
            case HAUT:
                return this.niveau.prepareCoup(-1, 0);
            case DROITE:
                return this.niveau.prepareCoup(0, 1);
            case BAS:
                return this.niveau.prepareCoup(1, 0);
            case GAUCHE:
                return this.niveau.prepareCoup(0, -1);
            default:
                return null;
        }
    }
    public void set_estVisite(boolean bool){
        this.estVisite = bool;
    }

    private EtatNiveauBis copy() {
        EtatNiveauBis newEtat = new EtatNiveauBis(this.niveau);
        newEtat.positionsDesCaisses = this.positionsDesCaisses;
        newEtat.positionsDesCibles = this.positionsDesCibles;
        newEtat.positionPousseur = this.positionPousseur;
        newEtat.estVisite = this.estVisite;
        return newEtat;
    }
}
