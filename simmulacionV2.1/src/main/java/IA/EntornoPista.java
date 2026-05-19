package IA;

import modelo.Vehiculo;
import motor.GestorColisiones;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public class EntornoPista implements MDP<EstadoVehiculo, Integer, DiscreteSpace> {

    private Vehiculo vehiculo;
    private GestorColisiones gestor;

    // Variables para el rastreo GPS anti-estancamiento
    private int pasosSobrevividos = 0;
    private double ultimoX = 0;
    private double ultimoY = 0;

    // El espacio de acciones: 3 opciones (0 = Recto, 1 = Izquierda, 2 = Derecha)
    private DiscreteSpace actionSpace = new DiscreteSpace(3);
    //double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestor);
    private double[] normalizados = new double[5];
    // El espacio de observación: 5 sensores
    private ObservationSpace<EstadoVehiculo> observationSpace = new ArrayObservationSpace<>(new int[]{5});
    double recompensa = 0;


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
        // Limpieza total y posicionamiento
        vehiculo.reiniciarPosicion();
        vehiculo.setEstado("En movimiento");
        vehiculo.actualizar();

        // Reiniciamos el GPS con las coordenadas frescas
        this.ultimoX = vehiculo.getX();
        this.ultimoY = vehiculo.getY();
        this.pasosSobrevividos = 0;

        // Lectura inicial de sensores
        double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestor);
        normalizados = new double[5];
        for(int i = 0; i < 5; i++) {
            normalizados[i] = distancias[i] / 500.0;
        }

        return new EstadoVehiculo(normalizados);
    }

    @Override
    public StepReply<EstadoVehiculo> step(Integer action) {

        int frameSkip = 5;
        boolean chocoEnElCamino = false;

        // ---------------------------------------------------------
        // FÍSICAS Y CINEMÁTICA
        // ---------------------------------------------------------
        for (int i = 0; i < frameSkip; i++) {
            if (action == 1) {
                vehiculo.girar(-3.0);
                vehiculo.acelerar();
            } else if (action == 2) {
                vehiculo.girar(3.0);
                vehiculo.acelerar();
            } else if (action == 0) {
                vehiculo.acelerar();
            }

            vehiculo.actualizar();

            // Verificamos si este micro-movimiento lo hizo chocar
            if (gestor.chocaConBorde(vehiculo)) {
                chocoEnElCamino = true;
                break;
            }
        }

        // Fricción por tomar curvas (pérdida de tracción)
        if (action == 1 || action == 2) {
            vehiculo.setVelocidad(vehiculo.getVelocidad() * 0.90);
        }

        // ---------------------------------------------------------
        // 2. LECTURA DE SENSORES
        // ---------------------------------------------------------
         double[] distancias = vehiculo.getSensores().obtenerDistancias(vehiculo, gestor);
        //normalizados = new double[5];
        for (int i = 0; i < 5; i++) {
            normalizados[i] = distancias[i] / 500.0;
        }

        // ---------------------------------------------------------
        // 3. LA ECONOMÍA DEL MIEDO (Reward Shaping Limpio)
        // ---------------------------------------------------------
        // 1. EL CASTIGO EXISTENCIAL: Existir cuesta dinero.
        // Esto obliga a la IA a buscar la meta lo más rápido posible.


        // 2. EL ÚNICO INGRESO: La velocidad.
        // Solo gana puntos si realmente está cubriendo terreno.
        recompensa += (vehiculo.getVelocidad() * 0.5);

        // 3. PENALIZACIÓN POR FRICCIÓN: Multa por zigzaguear o frenar el carro girando sin sentido.
//        if (action == 1 || action == 2) {
//            recompensa -= 0.5;
//        }

        // 4. EL INSTINTO DE SUPERVIVENCIA: Un pequeño "toque eléctrico" si se acerca demasiado a las paredes laterales.
//        if (normalizados[0] <= 0.15 || normalizados[4] <= 0.15) {
//            recompensa -= 2.0;
//        }

        recompensa += (vehiculo.getVelocidad() * 0.1);
        recompensa += 0.1;
        // ---------------------------------------------------------
        // 4. GPS INFALIBLE (Checkpoint cada 100 pasos)
        // ---------------------------------------------------------
        this.pasosSobrevividos++;

        if (this.pasosSobrevividos % 100 == 0) {
            double desplazamiento = Math.sqrt(Math.pow(vehiculo.getX() - ultimoX, 2) + Math.pow(vehiculo.getY() - ultimoY, 2));

            if (desplazamiento < 50.0) {
                System.out.println(">>> [ALERTA] Ejecución por GPS: Detectadas donas/estancamiento en la pista.");
                // Muere por ir demasiado lento o hacer círculos
                return new StepReply<>(new EstadoVehiculo(normalizados), -500.0, true, null);
            }

            // ¡ELIMINAMOS EL PREMIO DE +300!
            // El único premio por pasar el GPS es que no te mato.

            this.ultimoX = vehiculo.getX();
            this.ultimoY = vehiculo.getY();
        }
        //if(normalizados[0] <= 0.1 || normalizados[4] <= 0.1 ){recompensa -= 2;}

        // ---------------------------------------------------------
        // 5. COLISIONES FINALES (Bancarrota absoluta)
        // ---------------------------------------------------------
        boolean terminado = false;
        if (chocoEnElCamino || gestor.chocaConBorde(vehiculo)) {
            recompensa = -500.0; // Castigo letal
            vehiculo.setEstado("Chocado");
            terminado = true;
        } else if (gestor.cruzoLaMeta(vehiculo)) {
            System.out.println(">>> [META] ¡CARRERA COMPLETADA!");
            recompensa = 100000.0;
            vehiculo.setEstado("Meta");
            terminado = true;
        }

        return new StepReply<>(new EstadoVehiculo(normalizados), recompensa, terminado, null);
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