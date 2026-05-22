package IA;

import controlador.Controlador;
import modelo.Vehiculo;
import motor.GestorColisiones;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class AgenteIA implements Controlador {

    private MultiLayerNetwork cerebroIA;
    private int cooldownIA = 0;
    private int accionActualIA = 0;
    private boolean iaPensando = false;
    private GestorColisiones gestorFisicas;

    public AgenteIA(GestorColisiones gestor, String rutaModelo) {
        this.gestorFisicas = gestor;
        try {
            File archivo = new File(rutaModelo);
            if (archivo.exists()) {
                cerebroIA = MultiLayerNetwork.load(archivo, false);
                System.out.println(">>> Piloto IA está listo para correr.");
            } else {
                System.out.println(">>> [SISTEMA] No se encontro modelo entrenado en: " + rutaModelo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isCargado() {
        return cerebroIA != null;
    }
    @Override
    public void procesarAcciones(Vehiculo vehiculo) {
        if (cerebroIA == null) return;

        if (cooldownIA <= 0 && !iaPensando) {
            iaPensando = true;
            double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestorFisicas);

            CompletableFuture.runAsync(() -> {
                // SOLUCIÓN PUNTO 3: try-with-resources asegura que ND4J libere la memoria RAM nativa en cada frame
                try (INDArray entrada = Nd4j.create(new double[][]{distancias}).divi(200.0);
                     INDArray predicciones = cerebroIA.output(entrada)) {
                    accionActualIA = Nd4j.argMax(predicciones, 1).getInt(0);
                } finally {
                    iaPensando = false;
                }
            });
            cooldownIA = 5;
        }
        cooldownIA--;

        vehiculo.ejecutarAccionIA(accionActualIA);
    }
}
