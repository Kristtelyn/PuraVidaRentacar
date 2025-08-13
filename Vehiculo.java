package puravida;

public class Vehiculo {
    String placa, modelo;
    double tarifa;
    boolean disponible = true, mantenimiento = false;
    String reservadoPor = null;
    int diasReservados = 0;

    public Vehiculo(String p, String m, double t) {
        placa = p;
        modelo = m;
        tarifa = t;
    }

    @Override
    public String toString() {
        String est = mantenimiento ? "MANT." : (disponible ? "DISPONIBLE" : "OCUPADO");
        return placa + " - " + modelo + " ₡" + tarifa + "/dia [" + est + "]";
    }
}
