package Controleur;

import Modele.Coup;

public class CoupCaisse extends Coup {
    public Coup coup;
    boolean ABougeCaisse = false;

    public CoupCaisse(Coup coup, boolean ABougeCaisse) {
        this.coup = coup;
        this.ABougeCaisse = ABougeCaisse;
    }
}

