package Controleur;

import Global.Configuration;
import Modele.Coup;
import Modele.Niveau;
import Structures.Sequence;

import java.util.*;

class IA_naive extends IA {
    Random r;
    // Couleurs au format RGB (rouge, vert, bleu, un octet par couleur)
    final static int VERT = 0x00CC00;
    final static int MARRON = 0xBB7755;
    final static int ROUGE = 0xCC0000;

    // deplacements
    final static int HAUT = 0;
    final static int DROITE = 1;
    final static int BAS = 2;
    final static int GAUCHE = 3;

    private Hashtable<EtatNiveauBis, EtatNiveauBis> table;

    int cout = 0;
    int DistanceCaissesCibles = 0;

    class Priority implements Comparator<EtatPondere> {
        public int compare(EtatPondere e1, EtatPondere e2) {
            return e1.poids - e2.poids;
        }
    }

    @Override
    public Sequence<Coup> joue() {
        /* Sequence stockant le chemin complet depuis l'état initial vers la solution finale */
        Sequence<EtatNiveauBis> chemin = Configuration.instance().nouvelleSequence();
        /* FAP permettant de stocker les états suivants à visiter, priorité aux plus près de l'état initial + état solution */
        PriorityQueue<EtatPondere> file = new PriorityQueue<EtatPondere>(new Priority());
        /* Etat pondéré pour la FAP */
        EtatPondere courant = new EtatPondere(new EtatNiveauBis(this.niveau));
        /* Associe un état à sont état père (permet de retracer le chemin) */
        table = new Hashtable<EtatNiveauBis, EtatNiveauBis>();
        /* Initialisation avec null */
        table.put(courant.etatNiveau, new EtatNiveauBis());
        /* Etat précédent pour la table de hachage */
        EtatNiveauBis prec = null;
        /* Distance totale de chaque caisse à la cible la plus proche */
        int distances = 0;
        /* Si déplacement de caisse, on refait le calcul de distances */
        boolean aBougeCaisse = false;

        /* Etat pondere associé à l'état courant */
        EtatPondere pondere = new EtatPondere(courant.etatNiveau);
        /* Calcul de la distance dechaque caisse avec la cible la plus proche */
        distances = pondere.CalculDistances();

        boolean solutionTrouvee = false;

        /* Boucle tant que la solution n'est pas trouvée */
        while (!solutionTrouvee) {
            Niveau newNiveau;
            CoupCaisse coup;
            /* Si déplacement possible en haut */
            if ((coup = courant.etatNiveau.deplacement_possible(HAUT)) != null) {
                if (coup.ABougeCaisse)
                    aBougeCaisse = true;
                if (prec != null) {
                    table.put(courant.etatNiveau, prec);
                }
                /* Création du nouveau niveau avec le coup à jouer */
                newNiveau = courant.etatNiveau.niveau.clone();
                newNiveau.jouer(coup.coup);

                /* Création de l'état associé au nouveau niveau */
                EtatNiveauBis newEtat = new EtatNiveauBis(newNiveau);
                /* Etat pondéré du nouvel état */
                pondere = new EtatPondere(newEtat);
                /* Si une caisse a été bougé, on recalcul distances */
                if (aBougeCaisse) {
                    distances = pondere.CalculDistances();
                    aBougeCaisse = false;
                }
                /* Association du poids à l'état pondéré du nouveau niveau */
                pondere.poids = cout + 1 + distances;

                /* Si la case n'a pas déjà été visitée */
                if(!table.containsKey(newEtat))
                    file.add(pondere);
            }
            if ((coup = courant.etatNiveau.deplacement_possible(DROITE)) != null) {
                if (coup.ABougeCaisse)
                    aBougeCaisse = true;
                if (prec != null) {
                    table.put(courant.etatNiveau, prec);
                }
                // Ajout du nouvel état dans la FAP
                /* Ajouter une marque sur la position du pousseurs dans courant */
                newNiveau = courant.etatNiveau.niveau.clone();
                newNiveau.jouer(coup.coup);
                /* Ajout dans la FAP */
                EtatNiveauBis newEtat = new EtatNiveauBis(newNiveau);
                pondere = new EtatPondere(newEtat);
                /* Coût à modifier */
                if (aBougeCaisse) {
                    distances = pondere.CalculDistances();
                    aBougeCaisse = false;
                }
                pondere.poids = cout + 1 + distances;

                /* Si la case n'a pas déjà été visitée */
                if(!table.containsKey(newEtat))
                    file.add(pondere);
            }
            if ((coup = courant.etatNiveau.deplacement_possible(BAS)) != null) {
                if (coup.ABougeCaisse)
                    aBougeCaisse = true;
                if (prec != null) {
                    table.put(courant.etatNiveau, prec);
                }
                // Ajout du nouvel état dans la FAP
                /* Ajouter une marque sur la position du pousseurs dans courant */
                newNiveau = courant.etatNiveau.niveau.clone();
                newNiveau.jouer(coup.coup);
                /* Ajout dans la FAP */
                EtatNiveauBis newEtat = new EtatNiveauBis(newNiveau);
                pondere = new EtatPondere(newEtat);
                /* Coût à modifier */
                if (aBougeCaisse) {
                    distances = pondere.CalculDistances();
                    aBougeCaisse = false;
                }
                pondere.poids = cout + 1 + distances;

                /* Si la case n'a pas déjà été visitée */
                if(!table.containsKey(newEtat))
                    file.add(pondere);
            }
            if ((coup = courant.etatNiveau.deplacement_possible(GAUCHE)) != null) {
                if (coup.ABougeCaisse)
                    aBougeCaisse = true;
                if (prec != null) {
                    table.put(courant.etatNiveau, prec);
                }
                // Ajout du nouvel état dans la FAP
                newNiveau = courant.etatNiveau.niveau.clone();
                newNiveau.jouer(coup.coup);
                /* Ajout dans la FAP */
                EtatNiveauBis newEtat = new EtatNiveauBis(newNiveau);
                pondere = new EtatPondere(newEtat);
                /* Coût à modifier */
                if (aBougeCaisse) {
                    distances = pondere.CalculDistances();
                    aBougeCaisse = false;
                }
                newEtat.cout = cout + 1 + distances;

                /* Si la case n'a pas déjà été visitée */
                if(!table.containsKey(newEtat))
                    file.add(pondere);
            }
            cout++;
            prec = courant.etatNiveau;
            if (!file.isEmpty())
                courant = file.poll();
            if (estSolution(courant.etatNiveau))
                solutionTrouvee = true;
            else if(file.isEmpty())
                break;
        }
        if (solutionTrouvee) {
            // parcourt des états parents de courant jusqu'à trouver l'état initial
            while (courant.etatNiveau.niveau != null) {
                if(chemin == null)
                    chemin = Configuration.instance().nouvelleSequence();
                else
                    chemin.insereTete(courant.etatNiveau);
                courant.etatNiveau = table.get(courant.etatNiveau);
                table.remove(courant.etatNiveau);
            }
            Sequence<Coup> solution;
            if(chemin != null)
                solution = getChemin(chemin);
            else
                return null;
            return solution;
        } else
            return null;
    }

    public boolean estSolution(EtatNiveauBis etat) {
        for (int i = 0; i < etat.positionsDesCaisses.size(); i++) {
            if (!niveau.aBut(etat.positionsDesCaisses.get(i).x, etat.positionsDesCaisses.get(i).y)) {
                return false;
            }
        }
        return true;
    }

    public Coup getCoup(EtatNiveauBis courant, EtatNiveauBis suivant) {
        CoupCaisse coup;
        int dLig, dCol;
        dLig = suivant.positionPousseur.y - courant.positionPousseur.y;
        dCol = suivant.positionPousseur.x - courant.positionPousseur.x;
        coup = courant.niveau.prepareCoup(dLig, dCol);
        return coup.coup;
    }

    public Sequence<Coup> getChemin(Sequence<EtatNiveauBis> seq) {
        Sequence<Coup> chemin = Configuration.instance().nouvelleSequence();
        boolean debut = false;
        EtatNiveauBis courant = null;
        EtatNiveauBis suivant = null;
        while (!seq.estVide()) {
            if (!debut) {
                debut = true;
                courant = seq.extraitTete();
                suivant = seq.extraitTete();
            } else {
                suivant = seq.extraitTete();
            }
            Coup coup = getCoup(courant, suivant);
            chemin.insereQueue(coup);
            courant = suivant;
        }
        return chemin;
    }
}
