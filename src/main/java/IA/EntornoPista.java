package IA;

import modelo.Vehiculo;
import motor.GestorColisiones;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.factory.Nd4j;

public class EntornoPista implements MDP<EstadoVehiculo, Integer, DiscreteSpace> {

    private Vehiculo vehiculo;
    private GestorColisiones gestor;

    // Variables para el rastreo GPS anti-estancamiento
    private int pasosSobrevividos;
    private double ultimoX;
    private double ultimoY;

    // El espacio de acciones: 3 opciones (0 = Recto, 1 = Izquierda, 2 = Derecha)
    private DiscreteSpace actionSpace = new DiscreteSpace(3);

    // El espacio de observación: 5 sensores
    private ObservationSpace<EstadoVehiculo> observationSpace = new ArrayObservationSpace<>(new int[]{5});

    public EntornoPista(Vehiculo vehiculo, GestorColisiones gestor) {
        this.vehiculo = vehiculo;
        this.gestor = gestor;
    }

    @Override
    public ObservationSpace<EstadoVehiculo> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public EstadoVehiculo reset() {
        // 1. Limpieza total y posicionamiento inicial
        vehiculo.reiniciarPosicion();
        vehiculo.setEstado("En movimiento");
        vehiculo.actualizar();

        // 2. Reiniciamos el GPS con las coordenadas frescas (CRÍTICO)
        this.ultimoX = vehiculo.getX();
        this.ultimoY = vehiculo.getY();
        this.pasosSobrevividos = 0;

        // 3. Lectura inicial de sensores (Instanciamos un arreglo NUEVO)
        double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestor);
        double[] normalizados = new double[5];
        for(int i = 0; i < 5; i++) {
            normalizados[i] = distancias[i] / 200.0;
        }

        return new EstadoVehiculo(normalizados);
    }
    @Override
    public StepReply<EstadoVehiculo> step(Integer action) {

        double recompensaPaso = 0.0;
        int frameSkip = 5;
        boolean chocoEnElCamino = false;

        // 1. FÍSICAS Y CINEMÁTICA
        for (int i = 0; i < frameSkip; i++) {
            vehiculo.ejecutarAccionIA(action);
            vehiculo.actualizar();

            if (gestor.chocaConBorde(vehiculo)) {
                chocoEnElCamino = true;
                break;
            }
        }

        // 2. LECTURA DE SENSORES
        double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestor);
        double[] normalizados = new double[5];
        for (int i = 0; i < 5; i++) {
            normalizados[i] = distancias[i] / 200.0;
        }

        // 3. ABORTO INMEDIATO POR COLISIÓN
        if (chocoEnElCamino) {
            vehiculo.setEstado("Chocado");
            if (this.pasosSobrevividos % 100000 == 0) {
                org.nd4j.linalg.factory.Nd4j.getMemoryManager().invokeGc();
            }
            // CASTIGO BRUTAL: Queremos que evite los muros a toda costa
            return new StepReply<>(new EstadoVehiculo(normalizados), -5000.0, true, null);
        }

        // 4. NUEVO SISTEMA DE RECOMPENSAS
        this.pasosSobrevividos++;

        // Castigo constante por tiempo (obliga al agente a querer terminar rápido)
        recompensaPaso -= 1.0;

        // Recompensa vinculada estrictamente a la velocidad del motor
        recompensaPaso += (vehiculo.getVelocidad() * 5.0);

        // Castigo preventivo si el sensor central (frente) detecta un muro muy cerca
        if (normalizados[2] < 0.1) {
            recompensaPaso -= 50.0;
        }

        // 5. GPS ANTI-DONAS / ESTANCAMIENTO
        if (this.pasosSobrevividos % 15 == 0) {
            double desplazamiento = Math.sqrt(Math.pow(vehiculo.getX() - ultimoX, 2) + Math.pow(vehiculo.getY() - ultimoY, 2));

            // Si el coche da vueltas cerradas sobre su propio eje, el desplazamiento será casi cero
            if (desplazamiento < 15.0) {
                System.out.println(">>> [ALERTA] GPS: Auto estancado o dando vueltas. Abortando vida.");
                return new StepReply<>(new EstadoVehiculo(normalizados), -2000.0, true, null);
            }

            // Guardamos coordenadas para el siguiente control (Ya no damos bonos aquí para evitar abusos)
            this.ultimoX = vehiculo.getX();
            this.ultimoY = vehiculo.getY();
        }

        // 6. VERIFICACIÓN DE META
        boolean terminado = false;
        if (gestor.cruzoLaMeta(vehiculo)) {
            System.out.println(">>> [META] ¡CARRERA COMPLETADA POR LA IA!");
            recompensaPaso = 100000.0; // El tesoro final
            vehiculo.setEstado("Meta");
            terminado = true;
        }

        if (this.pasosSobrevividos % 10000 == 0) {
            org.nd4j.linalg.factory.Nd4j.getMemoryManager().invokeGc();
        }

        return new StepReply<>(new EstadoVehiculo(normalizados), recompensaPaso, terminado, null);
    }

    @Override
    public boolean isDone() {
        return vehiculo.getEstado().equals("Chocado") || vehiculo.getEstado().equals("Meta");
    }

    @Override
    public MDP<EstadoVehiculo, Integer, DiscreteSpace> newInstance() {
        return new EntornoPista(this.vehiculo, this.gestor);
    }

    @Override
    public void close() { }
}