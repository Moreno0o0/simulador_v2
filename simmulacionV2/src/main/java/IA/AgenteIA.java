package IA;

import controlador.Controlador;
import modelo.Vehiculo;
import motor.GestorColisiones;

public class AgenteIA implements Controlador {

    private GestorColisiones gestor;

    public AgenteIA(GestorColisiones gestor) {
        this.gestor = gestor;
    }
    @Override
    public void procesarAcciones(Vehiculo vehiculo) {

    }

}