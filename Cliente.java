package puravida;

public class Cliente {
    String cedula, nombre, telefono;

    public Cliente(String c, String n, String t) {
        cedula = c;
        nombre = n;
        telefono = t;
    }

    @Override
    public String toString() {
        return cedula + " - " + nombre + " (" + telefono + ")";
    }
}
