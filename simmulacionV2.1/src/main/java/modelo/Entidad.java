//Entidad base clase abstracta
package modelo;

public abstract class  Entidad {
    private double x;
    private double y;
    private double ancho;
    private double alto;

    public Entidad(double x, double y, double ancho, double alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }
    public abstract void actualizar();
    public double getX() { return x; }
    public double getY() {
        return y;
    }
    public double getAlto() {
        return alto;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }




    public double getAncho() {
        return ancho;
    }
}