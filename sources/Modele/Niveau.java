package Modele;
/*
 * Sokoban - Encore une nouvelle version (à but pédagogique) du célèbre jeu
 * Copyright (C) 2018 Guillaume Huard
 *
 * Ce programme est libre, vous pouvez le redistribuer et/ou le
 * modifier selon les termes de la Licence Publique Générale GNU publiée par la
 * Free Software Foundation (version 2 ou bien toute autre version ultérieure
 * choisie par vous).
 *
 * Ce programme est distribué car potentiellement utile, mais SANS
 * AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
 * commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
 * Licence Publique Générale GNU pour plus de détails.
 *
 * Vous devez avoir reçu une copie de la Licence Publique Générale
 * GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
 * États-Unis.
 *
 * Contact:
 *          Guillaume.Huard@imag.fr
 *          Laboratoire LIG
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */

import Global.Configuration;
import Structures.Iterateur;

public class Niveau implements Cloneable {
    static final int VIDE = 0;
    static final int MUR = 1;
    static final int POUSSEUR = 2;
    static final int CAISSE = 4;
    static final int BUT = 8;
    int l, c;
    int[][] cases;
    String nom;
    int pousseurL, pousseurC;
    int nbButs;
    int nbCaissesSurBut;

    Niveau() {
        cases = new int[1][1];
        l = c = 1;
        pousseurL = pousseurC = -1;
    }

    int ajuste(int cap, int objectif) {
        while (cap <= objectif) {
            cap = cap * 2;
        }
        return cap;
    }

    void redimensionne(int nouvL, int nouvC) {
        int capL = ajuste(cases.length, nouvL);
        int capC = ajuste(cases[0].length, nouvC);
        if ((capL > cases.length) || (capC > cases[0].length)) {
            int[][] nouvelles = new int[capL][capC];
            for (int i = 0; i < cases.length; i++)
                for (int j = 0; j < cases[0].length; j++)
                    nouvelles[i][j] = cases[i][j];
            cases = nouvelles;
        }
        if (nouvL >= l)
            l = nouvL + 1;
        if (nouvC >= c)
            c = nouvC + 1;
    }

    void fixeNom(String s) {
        nom = s;
    }

    void videCase(int i, int j) {
        redimensionne(i, j);
        cases[i][j] = VIDE;
    }

    void supprime(int contenu, int i, int j) {
        if (aBut(i, j)) {
            if (aCaisse(i, j) && ((contenu & CAISSE | contenu & BUT) != 0))
                nbCaissesSurBut--;
            if ((contenu & BUT) != 0)
                nbButs--;
        }
        if (aPousseur(i, j) && ((contenu & POUSSEUR) != 0))
            pousseurL = pousseurC = -1;
        cases[i][j] &= ~contenu;
    }

    void ajoute(int contenu, int i, int j) {
        redimensionne(i, j);
        int resultat = cases[i][j] | contenu;
        if ((resultat & BUT) != 0) {
            if (((resultat & CAISSE) != 0) && (!aCaisse(i, j) || !aBut(i, j)))
                nbCaissesSurBut++;
            if (!aBut(i, j))
                nbButs++;
        }
        if (((resultat & POUSSEUR) != 0) && !aPousseur(i, j)) {
            if (pousseurL != -1)
                throw new IllegalStateException("Attention trop de pousseurs !");
            pousseurL = i;
            pousseurC = j;
        }
        cases[i][j] = resultat;
    }

    int contenu(int i, int j) {
        return cases[i][j] & (POUSSEUR | CAISSE);
    }

    public void jouer(Coup c) {
        Iterateur<Mouvement> it = c.mouvements().iterateur();
        while (it.aProchain()) {
            Mouvement m = it.prochain();
            m.fixerContenu(contenu(m.depuisL(), m.depuisC()));
            supprime(m.contenu(), m.depuisL(), m.depuisC());
        }
        it = c.mouvements().iterateur();
        while (it.aProchain()) {
            Mouvement m = it.prochain();
            ajoute(m.contenu(), m.versL(), m.versC());
        }
        Iterateur<Marque> it2 = c.marques.iterateur();
        while (it2.aProchain()) {
            Marque m = it2.prochain();
            fixerMarque(m.valeur, m.ligne, m.colonne);
        }
    }

    public Coup creerCoup(int dLig, int dCol) {
        int destL = pousseurL + dLig;
        int destC = pousseurC + dCol;
        boolean caisse = aCaisse(destL, destC);
        Coup resultat = new Coup();

        if (caisse) {
            int dCaisL = destL + dLig;
            int dCaisC = destC + dCol;

            if (!aMur(dCaisL, dCaisC) && !aCaisse(dCaisL, dCaisC)) {
                resultat.deplace(destL, destC, dCaisL, dCaisC);
            } else {
                return null;
            }
        }
        if (!aMur(destL, destC)) {
            resultat.deplace(pousseurL, pousseurC, destL, destC);
            return resultat;
        }
        return null;
    }

    void ajouteMur(int i, int j) {
        ajoute(MUR, i, j);
    }

    void ajoutePousseur(int i, int j) {
        ajoute(POUSSEUR, i, j);
    }

    void ajouteCaisse(int i, int j) {
        ajoute(CAISSE, i, j);
    }

    void ajouteBut(int i, int j) {
        ajoute(BUT, i, j);
    }

    public int lignes() {
        return l;
    }

    public int colonnes() {
        return c;
    }

    public String nom() {
        return nom;
    }

    boolean estVide(int l, int c) {
        return cases[l][c] == VIDE;
    }

    public boolean aMur(int l, int c) {
        return (cases[l][c] & MUR) != 0;
    }

    public boolean aBut(int l, int c) {
        return (cases[l][c] & BUT) != 0;
    }

    public boolean aPousseur(int l, int c) {
        return (cases[l][c] & POUSSEUR) != 0;
    }

    public boolean aCaisse(int l, int c) {
        return (cases[l][c] & CAISSE) != 0;
    }

    public boolean estOccupable(int l, int c) {
        return !aCaisse(l, c) && !aMur(l, c);
    }

    public boolean estTermine() {
        return nbCaissesSurBut == nbButs;
    }

    public int lignePousseur() {
        return pousseurL;
    }

    public int colonnePousseur() {
        return pousseurC;
    }

    @Override
    public Niveau clone() {
        try {
            Niveau resultat = (Niveau) super.clone();
            resultat.cases = new int[this.cases.length][this.cases[0].length];

            int i = 0;
            while (i < this.cases.length) {
                resultat.cases[i] = this.cases[i].clone();
                i = i + 1;
            }

            return resultat;
        } catch (CloneNotSupportedException e) {
            Configuration.instance().logger().severe("Bug interne, pas clonable");
        }
        return null;
    }

    public int marque(int i, int j) {
        return (cases[i][j] >> 8) & 0xFFFFFF;
    }

    public void fixerMarque(int m, int i, int j) {
        cases[i][j] = (cases[i][j] & 0xFF) | (m << 8);
    }
}
