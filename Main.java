package puravida;

import javax.swing.JOptionPane;

public class Main {
    static final int MAX = 60;
    static Cliente[] clientes = new Cliente[MAX];
    static Vehiculo[] vehiculos = new Vehiculo[MAX];
    static int nCli = 0, nVeh = 0;

    public static void main(String[] args) {
        seed();
        boolean salir = false;
        while (!salir) {
            String menu = 
                "=== PURAVIDA RENTACAR ===\n" +
                "1) Clientes\n" +
                "2) Vehiculos\n" +
                "3) Reservas\n" +
                "4) Devoluciones / Factura\n" +
                "5) Consultar Vehiculos\n" +
                "0) Salir\n" +
                "Opcion:";
            String op = prompt(menu);
            if (op == null) { // Cancelar = salir
                salir = true;
                break;
            }
            switch (op.trim()) {
                case "1": menuClientes(); break;
                case "2": menuVehiculos(); break;
                case "3": menuReservas(); break;
                case "4": menuDevoluciones(); break;
                case "5": menuConsultaVehiculos(); break;
                case "0": salir = true; break;
                default: alert("Opcion invalida");
            }
        }
    }

    // Utilidades
    static String prompt(String msg) {
        return JOptionPane.showInputDialog(null, msg, "PuraVida Rentacar", JOptionPane.QUESTION_MESSAGE);
    }
    static void alert(String msg) {
        JOptionPane.showMessageDialog(null, msg, "PuraVida Rentacar", JOptionPane.INFORMATION_MESSAGE);
    }

    // === Clientes ===
    static void menuClientes() {
        boolean back = false;
        while (!back) {
            String op = prompt(
                "-- CLIENTES --\n" +
                "1) Registrar\n" +
                "2) Listar\n" +
                "0) Volver\n" +
                "Opcion:"
            );
            if (op == null) return;
            if (op.equals("1")) registrarCliente();
            else if (op.equals("2")) listarClientes();
            else if (op.equals("0")) back = true;
            else alert("Opcion invalida");
        }
    }
    static void registrarCliente() {
        if (nCli >= MAX) { alert("Sin espacio"); return; }
        String ced = prompt("Cedula:");
        if (ced == null || ced.isBlank()) return;
        if (buscarCliente(ced.trim()) != -1) { alert("Ya existe"); return; }
        String nom = prompt("Nombre:");
        if (nom == null || nom.isBlank()) return;
        String tel = prompt("Telefono:");
        if (tel == null || tel.isBlank()) return;
        clientes[nCli++] = new Cliente(ced.trim(), nom.trim(), tel.trim());
        alert("Cliente registrado");
    }
    static void listarClientes() {
        if (nCli == 0) { alert("No hay clientes"); return; }
        StringBuilder sb = new StringBuilder("CLIENTES:\n");
        for (int i = 0; i < nCli; i++) sb.append((i + 1)).append(") ").append(clientes[i]).append('\n');
        alert(sb.toString());
    }
    static int buscarCliente(String ced) {
        for (int i = 0; i < nCli; i++) if (clientes[i].cedula.equalsIgnoreCase(ced)) return i;
        return -1;
    }

    // Vehiculos
    static void menuVehiculos() {
        boolean back = false;
        while (!back) {
            String op = prompt(
                "-- VEHICULOS --\n" +
                "1) Registrar\n" +
                "2) Listar todos\n" +
                "3) Listar disponibles\n" +
                "4) Enviar a mantenimiento\n" +
                "5) Salir de mantenimiento\n" +
                "0) Volver\n" +
                "Opcion:"
            );
            if (op == null) return;
            switch (op.trim()) {
                case "1": registrarVehiculo(); break;
                case "2": listarVehiculos(false); break;
                case "3": listarVehiculos(true); break;
                case "4": cambiarMantenimiento(true); break;
                case "5": cambiarMantenimiento(false); break;
                case "0": back = true; break;
                default: alert("Opcion invalida");
            }
        }
    }
    static void registrarVehiculo() {
        if (nVeh >= MAX) { alert("Sin espacio"); return; }
        String pla = prompt("Placa:");
        if (pla == null || pla.isBlank()) return;
        if (buscarVehiculo(pla.trim()) != -1) { alert("Ya existe"); return; }
        String mod = prompt("Modelo:");
        if (mod == null || mod.isBlank()) return;
        Double t = leerDouble("Tarifa por dia:");
        if (t == null) return;
        vehiculos[nVeh++] = new Vehiculo(pla.trim(), mod.trim(), t);
        alert("Vehiculo registrado");
    }
    static void listarVehiculos(boolean soloDisp) {
        StringBuilder sb = new StringBuilder();
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (!soloDisp || (v.disponible && !v.mantenimiento)) {
                sb.append(v).append('\n'); hay = true;
            }
        }
        alert(hay ? sb.toString() : "No hay vehiculos para mostrar");
    }
    static void listarVehiculosOcupados() {
        StringBuilder sb = new StringBuilder();
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (!v.disponible && !v.mantenimiento && v.reservadoPor != null) {
                sb.append(v).append('\n'); hay = true;
            }
        }
        alert(hay ? sb.toString() : "No hay vehiculos ocupados actualmente.");
    }
    static int buscarVehiculo(String placa) {
        for (int i = 0; i < nVeh; i++) if (vehiculos[i].placa.equalsIgnoreCase(placa)) return i;
        return -1;
    }
    static void cambiarMantenimiento(boolean enviar) {
        String p = prompt("Placa:");
        if (p == null || p.isBlank()) return;
        int i = buscarVehiculo(p.trim());
        if (i == -1) { alert("No existe"); return; }
        Vehiculo v = vehiculos[i];
        if (enviar) { v.mantenimiento = true; v.disponible = false; alert("En mantenimiento"); }
        else { v.mantenimiento = false; if (v.reservadoPor == null) v.disponible = true; alert("Disponible"); }
    }

    // Reservas
    static void menuReservas() {
        boolean back = false;
        while (!back) {
            String op = prompt(
                "-- RESERVAS --\n" +
                "1) Crear reserva\n" +
                "2) Ver activas\n" +
                "3) Entregar vehiculo\n" +
                "0) Volver\n" +
                "Opcion:"
            );
            if (op == null) return;
            if (op.equals("1")) crearReserva();
            else if (op.equals("2")) verReservasActivas();
            else if (op.equals("3")) entregarVehiculo();
            else if (op.equals("0")) back = true;
            else alert("Opcion invalida");
        }
    }
    static void crearReserva() {
        alert("Vehiculos Disponibles:");
        listarVehiculos(true);
        String p = prompt("Placa:");
        if (p == null || p.isBlank()) return;
        int iv = buscarVehiculo(p.trim()); if (iv == -1) { alert("No existe"); return; }
        Vehiculo v = vehiculos[iv];
        if (!v.disponible || v.mantenimiento) { alert("No disponible"); return; }

        String c = prompt("Cedula cliente:");
        if (c == null || c.isBlank()) return;
        if (buscarCliente(c.trim()) == -1) { alert("Cliente no registrado"); return; }

        Integer d = leerEntero("Dias:");
        if (d == null || d <= 0) { alert("Dias invalidos"); return; }

        v.reservadoPor = c.trim();
        v.diasReservados = d;
        v.disponible = false;

        alert("Reserva creada para " + p.trim() + " por " + d + " dia(s)");
    }
    static void verReservasActivas() {
        StringBuilder sb = new StringBuilder();
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (v.reservadoPor != null) {
                sb.append(v.placa).append(" -> cliente ").append(v.reservadoPor)
                  .append(", ").append(v.diasReservados).append(" dia(s)\n");
                hay = true;
            }
        }
        alert(hay ? sb.toString() : "No hay reservas activas");
    }

    // entregar vehiculo
    static void entregarVehiculo() {
        String pla = prompt("Placa a ENTREGAR:");
        if (pla == null || pla.isBlank()) return;
        int i = buscarVehiculo(pla.trim());
        if (i == -1) { alert("No existe"); return; }
        Vehiculo v = vehiculos[i];
        if (v.reservadoPor == null) { alert("Ese vehiculo no tiene una reserva activa."); return; }
        if (!v.disponible) { alert("Ya fue entregado (OCUPADO)."); return; }
        v.disponible = false;
        alert("Vehiculo entregado correctamente.");
    }

    // Devoluciones / Factura
    static void menuDevoluciones() {
        StringBuilder sb = new StringBuilder("Vehiculos ocupados:\n");
        boolean Ocupados = false;
        for (int j = 0; j < nVeh; j++) {
            Vehiculo vehiculo = vehiculos[j];
            if (!vehiculo.disponible && vehiculo.reservadoPor != null && !vehiculo.mantenimiento) {
                sb.append(vehiculo.placa).append(" -- ").append(vehiculo.modelo)
                  .append(" (Cliente: ").append(vehiculo.reservadoPor).append(")\n");
                Ocupados = true;
            }
        }
        if (!Ocupados) { alert("No hay vehiculos por devolver."); return; }
        alert(sb.toString());

        String p = prompt("Placa a devolver:");
        if (p == null || p.isBlank()) return;
        int i = buscarVehiculo(p.trim());
        if (i == -1) { alert("No existe"); return; }
        Vehiculo v = vehiculos[i];
        if (v.reservadoPor == null) { alert("Este vehiculo no tiene reserva"); return; }

        double total = v.diasReservados * v.tarifa;
        String factura = "Facturacion\n" +
                "Placa: " + v.placa + "\n" +
                "Cliente: " + v.reservadoPor + "\n" +
                "Dias: " + v.diasReservados + "\n" +
                "Tarifa: " + v.tarifa + "\n" +
                "Total: " + total;
        alert(factura);

        v.reservadoPor = null;
        v.diasReservados = 0;
        v.disponible = true;
    }

    // Consulta Vehiculos
    static void menuConsultaVehiculos() {
        boolean back = false;
        while (!back) {
            String op = prompt(
                "-- CONSULTA DE VEHICULOS --\n" +
                "1) Ver disponibles\n" +
                "2) Ver ocupados\n" +
                "0) Volver\n" +
                "Opcion:"
            );
            if (op == null) return;
            switch (op.trim()) {
                case "1": listarVehiculos(true); break;
                case "2": listarVehiculosOcupados(); break;
                case "0": back = true; break;
                default: alert("Opcion invalida");
            }
        }
    }

    // Utilidades
    static Integer leerEntero(String msg) {
        while (true) {
            String s = prompt(msg);
            if (s == null) return null;
            try { return Integer.parseInt(s.trim()); }
            catch (Exception e) { msg = "Numero invalido, intente nuevamente:"; }
        }
    }
    static Double leerDouble(String msg) {
        while (true) {
            String s = prompt(msg);
            if (s == null) return null;
            try { return Double.parseDouble(s.trim()); }
            catch (Exception e) { msg = "Numero invalido, intente nuevamente:"; }
        }
    }

    // Datos iniciales
    static void seed() {
        vehiculos[nVeh++] = new Vehiculo("PVR-101", "Yaris", 28000);
        vehiculos[nVeh++] = new Vehiculo("PVR-202", "Accent", 30000);
        vehiculos[nVeh++] = new Vehiculo("PVR-303", "Rio", 27500);
        vehiculos[nVeh++] = new Vehiculo("PVR-404", "Versa", 29000);
    }

    
    static void elegirVehiculoYAsociarCliente() {
        alert("Vehiculos DISPONIBLES:");
        listarVehiculos(true);

        String pla = prompt("Placa:");
        if (pla == null || pla.isBlank()) return;
        int iv = buscarVehiculo(pla.trim());
        if (iv == -1) { alert("No existe"); return; }

        Vehiculo v = vehiculos[iv];
        if (!v.disponible || v.mantenimiento) { alert("No disponible"); return; }

        String ced = prompt("Cedula del cliente:");
        if (ced == null || ced.isBlank()) return;
        if (buscarCliente(ced.trim()) == -1) { alert("Cliente no registrado"); return; }

        Integer d = leerEntero("Dias:");
        if (d == null || d <= 0) { alert("Dias invalidos"); return; }

        v.reservadoPor = ced.trim();
        v.diasReservados = d;
        v.disponible = false;
        alert("Reserva creada para " + ced.trim() + " por " + d + " dia(s).");
    }
}
