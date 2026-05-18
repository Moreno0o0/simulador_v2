package IA;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.nd4j.linalg.learning.config.Adam;
import java.io.File;

public class EntrenadorIA {

    public static void entrenar(EntornoPista entorno, String nombreHilo) {
        // Configuracion ESTÁNDAR, sin async
        QLearningConfiguration configQL = QLearningConfiguration.builder()
                .seed(123L)
                .maxEpochStep(1000)
                .maxStep(50000)
                .expRepMaxSize(1000)
                .batchSize(128)          // Lotes grandes para aprovechar CPU
                .targetDqnUpdateFreq(500)
                .updateStart(0)
                .rewardFactor(0.01)
                .gamma(0.99)
                .errorClamp(1.0)
                .minEpsilon(0.1f)
                .epsilonNbStep(50000)
                .doubleDQN(true)
                .build();

        DQNDenseNetworkConfiguration configRed = DQNDenseNetworkConfiguration.builder()
                .l2(0.0001)
                .updater(new Adam(0.001))
                .numHiddenNodes(256)
                .numLayers(3)
                .build();

        try {
            // Cada hilo guarda su propio modelo para que no choquen al guardar

            String nombreArchivo = "modelo_entrenado" + nombreHilo + ".zip";
            File archivoModelo = new File(nombreArchivo);

            QLearningDiscreteDense<EstadoVehiculo> dql;

            if (archivoModelo.exists()) {
                MultiLayerNetwork redCargada = MultiLayerNetwork.load(archivoModelo, true);
                dql = new QLearningDiscreteDense<>(entorno, new DQN(redCargada), configQL);
            } else {
                dql = new QLearningDiscreteDense<>(entorno, configRed, configQL);
            }

            dql.train();
            dql.getNeuralNet().save(nombreArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}