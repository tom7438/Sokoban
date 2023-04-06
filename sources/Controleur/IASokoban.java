package Controleur;

import Global.Configuration;
import Modele.Coup;
import Modele.Niveau;
import Structures.Sequence;

import java.util.Hashtable;

class IASokoban extends IA {

    // deplacements
    final static int HAUT = 0;
    final static int GAUCHE = 1;
    final static int BAS = 2;
    final static int DROITE = 3;

    final static int ROUGE = 0xCC0000;

    @Override
    public void initialise() {
        Configuration.instance().logger().info("Demarrage de l'IA sur un niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
    }

    public Sequence<Coup> resoudreSokoban() {
        /* Compteur du nombre de tentatives */
        int nbEtat = 1;
        /* Caisse courante */
        int caisseCourante;
        /* Booléen vrai si la solution est trouvée */
        boolean solutionTrouvee = false;
        /* Etat du niveau visité et le prochain à visiter */
        EtatNiveau etatVisite, etatAVisiter;
        /* Sequence de coups représentant la solution */
        Sequence<Coup> solution = Configuration.instance().nouvelleSequence();
        /* Sequence d'état à visiter */
        Sequence<EtatNiveau> sequenceEtatAVisiter = Configuration.instance().nouvelleSequence();
        /* Association entre un état et son état précédent */
        Hashtable<EtatNiveau, EtatNiveauPrecedent> hashtable = new Hashtable<EtatNiveau, EtatNiveauPrecedent>();

        /* Initialisation de l'état à visiter */
        etatAVisiter = new EtatNiveau(niveau);
        /* Association entre l'état initial et un état null */
        hashtable.put(etatAVisiter, new EtatNiveauPrecedent(null, false));
        /* On insère l'état à visiter dans la séquence */
        sequenceEtatAVisiter.insereQueue(etatAVisiter);

        /* Recherche de solution + Vérification validité */
        while (!solutionTrouvee && !sequenceEtatAVisiter.estVide()) {
            Configuration.instance().logger().info("Tentative " + nbEtat);
            /* Le 1er état à visiter devient l'état visité */
            etatVisite = sequenceEtatAVisiter.extraitTete();
            /* On marque l'état visité comme visité */
            hashtable.get(etatVisite).etatVisitee = true;

            /* Fonction auxiliaire qui renvoie vrai si toutes les caisses sont sur les buts */
            solutionTrouvee = CaissesSurBut(etatVisite);

            /* Si l'état précédent n'était pas une solution */
            if (!solutionTrouvee) {

                /* Déplacement HAUT */
                if (etatVisite.deplacementPossible(HAUT, niveau)) {
                    etatAVisiter = etatVisite.etatNiveauApresDeplacement(HAUT);

                    /* Si non present dans la table de hashage */
                    if (!hashtable.containsKey(etatAVisiter)) {
                        /* Insertion dans la table de hashage */
                        hashtable.put(etatAVisiter, new EtatNiveauPrecedent(etatVisite, false));
                        /* Insertion dans la sequence d'état à parcourir */
                        sequenceEtatAVisiter.insereQueue(etatAVisiter);
                    }
                }

                /* Déplacement GAUCHE */
                if (etatVisite.deplacementPossible(GAUCHE, niveau)) {
                    etatAVisiter = etatVisite.etatNiveauApresDeplacement(GAUCHE);

                    /* Si non present dans la table de hashage */
                    if (!hashtable.containsKey(etatAVisiter)) {
                        /* Insertion dans la table de hashage */
                        hashtable.put(etatAVisiter, new EtatNiveauPrecedent(etatVisite, false));
                        /* Insertion dans la sequence d'état à parcourir */
                        sequenceEtatAVisiter.insereQueue(etatAVisiter);
                    }
                }

                /* Déplacement BAS */
                if (etatVisite.deplacementPossible(BAS, niveau)) {
                    etatAVisiter = etatVisite.etatNiveauApresDeplacement(BAS);

                    /* Si non present dans la table de hashage */
                    if (!hashtable.containsKey(etatAVisiter)) {
                        /* Insertion dans la table de hashage */
                        hashtable.put(etatAVisiter, new EtatNiveauPrecedent(etatVisite, false));
                        /* Insertion dans la sequence d'état à parcourir */
                        sequenceEtatAVisiter.insereQueue(etatAVisiter);
                    }
                }

                /* Déplacement DROITE */
                if (etatVisite.deplacementPossible(DROITE, niveau)) {
                    etatAVisiter = etatVisite.etatNiveauApresDeplacement(DROITE);

                    /* Si non present dans la table de hashage */
                    if (!hashtable.containsKey(etatAVisiter)) {
                        /* Insertion dans la table de hashage */
                        hashtable.put(etatAVisiter, new EtatNiveauPrecedent(etatVisite, false));
                        /* Insertion dans la sequence d'état à parcourir */
                        sequenceEtatAVisiter.insereQueue(etatAVisiter);
                    }
                }
            } else {
                /* Si une solution a été trouvée */
                Sequence<EtatNiveau> sequenceSolution = Configuration.instance().nouvelleSequence();

                /* On renverse la pile d'état */
                while (etatVisite != null) {
                    sequenceSolution.insereTete(etatVisite);
                    etatVisite = hashtable.get(etatVisite).etatPrecedent;
                }

                /* On transforme la séquence d'état en séquence de coup */
                solution = NiveauToCoup(sequenceSolution, niveau, solution);
            }
            /* Si la solution n'est pas trouvée, on incrémente le nombre de tentatives */
            if (!solutionTrouvee) {
                nbEtat++;
            }
        }
        /* Si aucune solution n'a été trouvée */
        if (!solutionTrouvee) {
            solution = null;
        }
        return solution;
    }

    @Override
    public Sequence<Coup> joue() {

        Sequence<Coup> solution = this.resoudreSokoban();
        if (solution == null) {
            Configuration.instance().logger().info("Le niveau n'a pas de solution pour le niveau " + niveau.nom());
            niveauSuivant();
        }
        return solution;
    }

    @Override
    public void finalise() {
        Configuration.instance().logger().info("Fin de traitement du niveau par l'IA");
    }

    public boolean CaissesSurBut(EtatNiveau etatVisite) {
        /* On commence à la caisse 0 */
        int caisseCourante = 0;
        while (caisseCourante < etatVisite.positionsDesCaisses.size()) {
            /* S'il n'y a pas de But sur la même case que la caisse */
            if (!niveau.aBut(etatVisite.positionsDesCaisses.get(caisseCourante).y, etatVisite.positionsDesCaisses.get(caisseCourante).x)) {
                return false;
            }
            caisseCourante++;
        }
        return true;
    }

    public Sequence<Coup> NiveauToCoup(Sequence<EtatNiveau> sequenceSolution, Niveau niveau, Sequence<Coup> solution) {
        Coup coup;
        /* On récupère le premier état de la séquence */
        EtatNiveau etatPrecedentSequence = sequenceSolution.extraitTete();
        /* Future etatActuel */
        EtatNiveau etatActuelSequence;
        /* On crée une copie du niveau */
        Niveau copieNiveau = niveau.clone();

        /* Transformation état Niveau en Coup */
        // TODO : fonction auxiliaire
        // sequenceSolution + copieNiveau + solution
        while (!sequenceSolution.estVide()) {
            /* Assignation de l'état actuel */
            etatActuelSequence = sequenceSolution.extraitTete();
            coup = copieNiveau.creerCoup(etatActuelSequence.positionDuPousseur.y - etatPrecedentSequence.positionDuPousseur.y, etatActuelSequence.positionDuPousseur.x - etatPrecedentSequence.positionDuPousseur.x);
            // TODO : Ajout potentiel de marque
            coup.marque(etatPrecedentSequence.positionDuPousseur.y, etatPrecedentSequence.positionDuPousseur.x, ROUGE);
            /* Insertion en queue */
            solution.insereQueue(coup);
            /* On joue le coup sur la copie du niveau */
            copieNiveau.jouer(coup);
            /* On assigne l'état actuel comme état précédent */
            etatPrecedentSequence = etatActuelSequence;
        }
        return solution;
    }
}