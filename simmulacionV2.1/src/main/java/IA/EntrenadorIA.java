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

        QLearningConfiguration configQL = QLearningConfiguration.builder()
                .seed(123L)
                .maxEpochStep(7000)        // Si da 2000 pasos sin meta/choque, reiniciamos (evita bucles infinitos)
                .maxStep(400000)           // ¡MEDIO MILLÓN de pasos de entrenamiento total!
                .expRepMaxSize(60000)      // Memoria de elefante: recuerda sus últimos 50,000 pasos
                .batchSize(64)             // Lotes más pequeños para redes pequeñas (aprende más rápido)
                .targetDqnUpdateFreq(1500)  // Frecuencia de actualización de la red objetivo
                .updateStart(5000)         // Espera a tener 1000 recuerdos antes de empezar a estudiar
                .rewardFactor(1.0)         // ¡Dejamos los puntos tal cual se los mandas desde EntornoPista!
                .gamma(0.99)               // Visión a largo plazo intacta
                .errorClamp(1.0)
                .minEpsilon(0.05f)         // Al final de su vida, usará su cerebro el 95% del tiempo
                .epsilonNbStep(100000)     // Explora (hace locuras) durante los primeros 100k pasos
                .doubleDQN(true)
                .build();

        // EL CEREBRO ÁGIL: Pequeño, rápido y mortal
        DQNDenseNetworkConfiguration configRed = DQNDenseNetworkConfiguration.builder()
                .l2(0.0001)
                .updater(new Adam(0.00025))
                .numHiddenNodes(128)       // Reducido de 512 a 128 (Tu procesador te lo agradecerá)
                .numLayers(4)              // Reducido de 6 a 3 capas (Perfecto para 5 sensores)
                .build();

        try {
            String nombreArchivo = "modelo_entrenado" + nombreHilo + ".zip";
            File archivoModelo = new File(nombreArchivo);

            QLearningDiscreteDense<EstadoVehiculo> dql;

            if (archivoModelo.exists()) {
                MultiLayerNetwork redCargada = MultiLayerNetwork.load(archivoModelo, true);
                dql = new QLearningDiscreteDense<>(entorno, new DQN(redCargada), configQL);
                System.out.println(">>> [SISTEMA] Continuando entrenamiento de modelo existente...");
            } else {
                dql = new QLearningDiscreteDense<>(entorno, configRed, configQL);
                System.out.println(">>> [SISTEMA] Iniciando entrenamiento desde cero...");
            }

            dql.train();
            dql.getNeuralNet().save(nombreArchivo);
            System.out.println(">>> [ÉXITO] Entrenamiento finalizado. Cerebro guardado en " + nombreArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}