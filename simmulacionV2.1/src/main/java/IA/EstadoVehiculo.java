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
        return Nd4j.create(distancias);
    }

    @Override
    public Encodable dup() {
        return new EstadoVehiculo(this.distancias.clone());
    }
}