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
package Controleur;

import Global.Configuration;
import Modele.Coup;
import Modele.Jeu;
import Modele.Niveau;
import Structures.Sequence;

abstract class IA {
	private Jeu jeu;
	Niveau niveau;

	static IA nouvelle(Jeu j) {
		IA instance = null;
		String name = Configuration.instance().lis("IA");
		try {
			instance = (IA) ClassLoader.getSystemClassLoader().loadClass(name).newInstance();
			instance.jeu = j;
		} catch (Exception e) {
			Configuration.instance().logger().severe("Impossible de trouver l'IA : " + name);
		}
		return instance;
	}

	final Sequence<Coup> elaboreCoups() {
		niveau = jeu.niveau().clone();
		return joue();
	}

	final void activeIA() {
		niveau = jeu.niveau().clone();
		initialise();
	}

	void initialise() {
	}

	Sequence<Coup> joue() {
		return null;
	}

	void finalise() {
	}

    void niveauSuivant() {
        jeu.prochainNiveau();
    }
}
