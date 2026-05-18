package IA;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class EstadoVehiculo implements Encodable {

    private double[] distancias;

    public EstadoVehiculo(double[] distancias) {
        this.distancias = distancias;
    }

    @Override
    public double[] toArray() {
        return distancias;
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        // VITAL: Crea el tensor matemático que la red neuronal consume
        return Nd4j.create(distancias);
    }

    @Override
    public Encodable dup() {
        // VITAL: Crea una copia exacta para la memoria de la IA
        return new EstadoVehiculo(this.distancias.clone());
    }
}