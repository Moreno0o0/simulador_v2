package vista; // Asegúrate de que coincida con tu paquete

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;



import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class VistaMenu extends StackPane { // Heredamos de StackPane para apilar fondo y botones

    public VistaMenu(Runnable accionJugar) {

        // 1. Imagen de fondo (La pista)
        Image imgFondo = new Image(getClass().getResourceAsStream("/assets/track.png"));
        ImageView vistaFondo = new ImageView(imgFondo);
        vistaFondo.setFitWidth(modelo.Constantes.ANCHO_VENTANA);
        vistaFondo.setFitHeight(modelo.Constantes.ALTO_VENTANA);
        vistaFondo.setOpacity(0.35); // La oscurecemos para que no estorbe al texto

        this.setStyle("-fx-background-color: #1a1a1a;"); // Fondo negro base

        // 2. Contenedor central
        VBox cajaCentral = new VBox(50);
        cajaCentral.setAlignment(Pos.CENTER);

        // 3. Título del Juego (Sombra roja neón)
        Label titulo = new Label("CARRERA NEURONAL");
        titulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titulo.setFont(new Font("Impact", 80));

        DropShadow sombraTexto = new DropShadow();
        sombraTexto.setColor(Color.RED);
        sombraTexto.setRadius(15);
        titulo.setEffect(sombraTexto);

        // 4. Botón Moderno con gradiente
        Button btnJugar = new Button("INICIAR COMPETENCIA");
        btnJugar.setFont(new Font("Arial Rounded MT Bold", 26));

        String estiloBase = "-fx-background-color: linear-gradient(#e74c3c, #c0392b);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 15 50 15 50;" +
                "-fx-background-radius: 40;" +
                "-fx-cursor: hand;";

        String estiloHover = "-fx-background-color: linear-gradient(#ff7979, #eb4d4b);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 15 50 15 50;" +
                "-fx-background-radius: 40;" +
                "-fx-cursor: hand;";

        btnJugar.setStyle(estiloBase);

        // Animaciones Hover
        btnJugar.setOnMouseEntered(e -> btnJugar.setStyle(estiloHover));
        btnJugar.setOnMouseExited(e -> btnJugar.setStyle(estiloBase));

        btnJugar.setOnAction(evento -> accionJugar.run());

        // 5. Ensamblamos todo en capas
        cajaCentral.getChildren().addAll(titulo, btnJugar);
        this.getChildren().addAll(vistaFondo, cajaCentral);
    }
}