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
package Global;

import Structures.Sequence;
import Structures.SequenceListe;
import Structures.SequenceTableau;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
	static Configuration instance = null;
	Properties prop;
	Logger logger;

	public static Configuration instance() {
		if (instance == null)
			instance = new Configuration();
		return instance;
	}

	public static InputStream charge(String nom) {
		return ClassLoader.getSystemClassLoader().getResourceAsStream(nom);
	}

	static void chargerProprietes(Properties p, InputStream in, String nom) {

		try {
			p.load(in);
		} catch (IOException e) {
			System.err.println("Impossible de charger " + nom);
			System.err.println(e.toString());
			System.exit(1);
		}
	}

	protected Configuration() {
		InputStream in = charge("defaut.cfg");
		Properties defaut = new Properties();
		chargerProprietes(defaut, in, "defaut.cfg");
		String message = "Fichier de proprietes defaut.cfg charge";
		String nom = System.getProperty("user.home") + "/.sokoban";
		try {
			in = new FileInputStream(nom);
			prop = new Properties(defaut);
			chargerProprietes(prop, in, nom);
			logger().info(message);
			logger().info("Fichier de proprietes " + nom + " charge");
		} catch (FileNotFoundException e) {
			prop = defaut;
			logger().info(message);
		}
	}

	public <E> Sequence<E> nouvelleSequence() {
		Sequence<E> resultat;
		String type = lis("Sequence");
		switch (type) {
		case "Liste":
			resultat = new SequenceListe<>();
			break;
		case "Tableau":
			resultat = new SequenceTableau<>();
			break;
		default:
			throw new NoSuchElementException("Sequences de type " + type + " non supportees");
		}
		return resultat;
	}

	public String lis(String nom) {
		String value = prop.getProperty(nom);
		if (value != null) {
			return value;
		} else {
			throw new NoSuchElementException("Propriete " + nom + " manquante");
		}
	}

	public Logger logger() {
		if (logger == null) {
			System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s : %5$s%n");
			logger = Logger.getLogger("Sokoban.Logger");
			logger.setLevel(Level.parse(lis("LogLevel")));
		}
		return logger;
	}
}
