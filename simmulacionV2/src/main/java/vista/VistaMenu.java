package vista; // Asegúrate de que coincida con tu paquete

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class VistaMenu extends VBox {

    // El constructor recibe una "acción" que se ejecutará al darle clic al botón
    public VistaMenu(Runnable accionJugar) {

        // 1. Configurar el fondo (Color oscuro para que resalte)
        this.setStyle("-fx-background-color: #2b2b2b;");
        this.setAlignment(Pos.CENTER); // Centrar todo
        this.setSpacing(40); // Espacio entre el título y el botón

        // 2. Crear el Título
        Label titulo = new Label("SIMULADOR DE CARRERAS");
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titulo.setFont(new Font("Arial", 50));

        // 3. Crear el Botón de Jugar
        Button btnJugar = new Button("INICIAR MOTOR");
        btnJugar.setFont(new Font("Arial", 24));
        btnJugar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

        // 4. ¿Qué pasa al hacer clic? Ejecutamos la acción que nos pasaron
        btnJugar.setOnAction(evento -> {
            accionJugar.run();
        });

        // 5. Agregar los elementos a la pantalla
        this.getChildren().addAll(titulo, btnJugar);
    }
}