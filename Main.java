package puravida;

import java.util.Scanner;

public class Main {
    static final int MAX = 60;
    static Cliente[] clientes = new Cliente[MAX];
    static Vehiculo[] vehiculos = new Vehiculo[MAX];
    static int nCli = 0, nVeh = 0;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        seed();
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== PURAVIDA RENTACAR ===");
            System.out.println("1) Clientes");
            System.out.println("2) Vehiculos");
            System.out.println("3) Reservas");
            System.out.println("4) Devoluciones / Factura");
            System.out.println("5) Consultar Vehículos");
            System.out.println("0) Salir");
            System.out.print("Opcion: ");
            switch (sc.nextLine()) {
                case "1": menuClientes(); break;
                case "2": menuVehiculos(); break;
                case "3": menuReservas(); break;
                case "4": menuDevoluciones(); break;
                case "5": menuConsultaVehiculos(); break;
                case "0": salir = true; break;
                default: System.out.println("Opcion invalida");
            }
        }
    }

    // Clientes
    static void menuClientes() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- CLIENTES --");
            System.out.println("1) Registrar");
            System.out.println("2) Listar");
            System.out.println("0) Volver");
            System.out.print("Opcion: ");
            String op = sc.nextLine();
            if (op.equals("1")) registrarCliente();
            else if (op.equals("2")) listarClientes();
            else if (op.equals("0")) back = true;
            else System.out.println("Invalida");
        }
    }
    static void registrarCliente() {
        if (nCli >= MAX) { System.out.println("Sin espacio"); return; }
        System.out.print("Cedula: "); String ced = sc.nextLine().trim();
        if (buscarCliente(ced) != -1) { System.out.println("Ya existe"); return; }
        System.out.print("Nombre: "); String nom = sc.nextLine().trim();
        System.out.print("Telefono: "); String tel = sc.nextLine().trim();
        clientes[nCli++] = new Cliente(ced, nom, tel);
        System.out.println("Cliente registrado");
    }
    static void listarClientes() {
        if (nCli == 0) { System.out.println("No hay clientes"); return; }
        for (int i = 0; i < nCli; i++) System.out.println((i + 1) + ") " + clientes[i]);
    }
    static int buscarCliente(String ced) {
        for (int i = 0; i < nCli; i++) if (clientes[i].cedula.equalsIgnoreCase(ced)) return i;
        return -1;
    }

    // Vehiculos
    static void menuVehiculos() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- VEHICULOS --");
            System.out.println("1) Registrar");
            System.out.println("2) Listar todos");
            System.out.println("3) Listar disponibles");
            System.out.println("4) Enviar a mantenimiento");
            System.out.println("5) Salir de mantenimiento");
            System.out.println("0) Volver");
            System.out.print("Opcion: ");
            switch (sc.nextLine()) {
                case "1": registrarVehiculo(); break;
                case "2": listarVehiculos(false); break;
                case "3": listarVehiculos(true); break;
                case "4": cambiarMantenimiento(true); break;
                case "5": cambiarMantenimiento(false); break;
                case "0": back = true; break;
                default: System.out.println("Invalida");
            }
        }
    }
    static void registrarVehiculo() {
        if (nVeh >= MAX) { System.out.println("Sin espacio"); return; }
        System.out.print("Placa: "); String pla = sc.nextLine().trim();
        if (buscarVehiculo(pla) != -1) { System.out.println("Ya existe"); return; }
        System.out.print("Modelo: "); String mod = sc.nextLine().trim();
        System.out.print("Tarifa por dia: "); double t = leerDouble();
        vehiculos[nVeh++] = new Vehiculo(pla, mod, t);
        System.out.println("Vehiculo registrado");
    }
    static void listarVehiculos(boolean soloDisp) {
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (!soloDisp || (v.disponible && !v.mantenimiento)) {
                System.out.println(v); hay = true;
            }
        }
        if (!hay) System.out.println("No hay vehiculos para mostrar");
    }
    static void listarVehiculosOcupados() {
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (!v.disponible && !v.mantenimiento && v.reservadoPor != null) {
                System.out.println(v); hay = true;
            }
        }
        if (!hay) System.out.println("No hay vehículos ocupados actualmente.");
    }

    static int buscarVehiculo(String placa) {
        for (int i = 0; i < nVeh; i++) if (vehiculos[i].placa.equalsIgnoreCase(placa)) return i;
        return -1;
    }
    static void cambiarMantenimiento(boolean enviar) {
        System.out.print("Placa: "); String p = sc.nextLine().trim();
        int i = buscarVehiculo(p); if (i == -1) { System.out.println("No existe"); return; }
        Vehiculo v = vehiculos[i];
        if (enviar) { v.mantenimiento = true; v.disponible = false; System.out.println("En mantenimiento"); }
        else { v.mantenimiento = false; if (v.reservadoPor == null) v.disponible = true; System.out.println("Disponible"); }
    }

    // Reservas
    static void menuReservas() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- RESERVAS --");
            System.out.println("1) Crear reserva");
            System.out.println("2) Ver activas");
            System.out.println("3) Entregar vehiculo");
            System.out.println("0) Volver");
            System.out.print("Opcion: ");
            String op = sc.nextLine();
            if (op.equals("1")) crearReserva();
            else if (op.equals("2")) verReservasActivas();
            else if (op.equals("3")) entregarVehiculo();
            else if (op.equals("0")) back = true;
            else System.out.println("Invalida");
        }
    }
    static void crearReserva() {
        System.out.println("Vehiculos Disponibles:");
        listarVehiculos(true);
        System.out.print("Placa: "); String p = sc.nextLine().trim();
        int iv = buscarVehiculo(p); if (iv == -1) { System.out.println("No existe"); return; }
        Vehiculo v = vehiculos[iv];
        if (!v.disponible || v.mantenimiento) { System.out.println("No disponible"); return; }

        System.out.print("Cedula cliente: "); String c = sc.nextLine().trim();
        if (buscarCliente(c) == -1) { System.out.println("Cliente no registrado"); return; }

        System.out.print("Dias: "); int d = leerEntero(); if (d <= 0) { System.out.println("Dias invalidos"); return; }
        
        v.reservadoPor = c; 
        v.diasReservados = d; 
        v.disponible = false;

        System.out.println("Reserva creada para " + p + " por " + d + " dia(s)");
    }
    static void verReservasActivas() {
        boolean hay = false;
        for (int i = 0; i < nVeh; i++) {
            Vehiculo v = vehiculos[i];
            if (v.reservadoPor != null) {
                System.out.println(v.placa + " -> cliente " + v.reservadoPor + ", " + v.diasReservados + " dia(s)");
                hay = true;
            }
        }
        if (!hay) System.out.println("No hay reservas activas");
    }

//entregar vehiculo
    static void entregarVehiculo() {
        System.out.print("Placa a ENTREGAR: ");
        String pla = sc.nextLine().trim();
        int i = buscarVehiculo(pla);
        if (i == -1) { System.out.println("No existe"); return; }

        Vehiculo v = vehiculos[i];
        if (v.reservadoPor == null) {
            System.out.println("Ese vehículo no tiene una reserva activa.");
            return;
        }
        if (!v.disponible) {
            System.out.println("Ya fue entregado (OCUPADO).");
            return;
        }
        v.disponible = false; 
        System.out.println("Vehículo entregado correctamente.");
    }

    // Devoluciones
       static void menuDevoluciones() {
        System.out.println("\nVehículos ocupados:");
    boolean Ocupados = false;
    for (int j = 0; j < nVeh; j++) {
        Vehiculo vehiculo = vehiculos[j];
        if (!vehiculo.disponible && vehiculo.reservadoPor != null && !vehiculo.mantenimiento) {
            System.out.println(vehiculo.placa + " -- " + vehiculo.modelo + " (Cliente: " + vehiculo.reservadoPor + ")");
            Ocupados = true;
        }
    }
    if (!Ocupados) {
        System.out.println("No hay vehículos por devolver.");
        return;
    }
    
    System.out.print("Placa a devolver: "); 
    String p = sc.nextLine().trim();
    int i = buscarVehiculo(p); 
    if (i == -1) { 
        System.out.println("No existe"); 
        return; 
    }
    Vehiculo v = vehiculos[i];
    if (v.reservadoPor == null) { 
        System.out.println("Este vehiculo no tiene reserva"); 
        return; 
    }

    double total = v.diasReservados * v.tarifa;
    System.out.println("\nFacturacion");
    System.out.println("Placa: " + v.placa);
    System.out.println("Cliente: " + v.reservadoPor);
    System.out.println("Dias: " + v.diasReservados);
    System.out.println("Tarifa: " + v.tarifa);
    System.out.println("Total: " + total);

    
    v.reservadoPor = null; 
    v.diasReservados = 0; 
    v.disponible = true;
}

// Consulta Vehículos
    static void menuConsultaVehiculos() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- CONSULTA DE VEHÍCULOS --");
            System.out.println("1) Ver disponibles");
            System.out.println("2) Ver ocupados");
            System.out.println("0) Volver");
            System.out.print("Opcion: ");
            switch (sc.nextLine()) {
                case "1": listarVehiculos(true); break;
                case "2": listarVehiculosOcupados(); break;
                case "0": back = true; break;
                default: System.out.println("Opción inválida");
            }
        }
    }


    // Utilidades
    static int leerEntero() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Numero invalido, intente: "); }
        }
    }
    static double leerDouble() {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (Exception e) { System.out.print("Numero invalido, intente: "); }
        }
    }

    // Datos iniciales
    static void seed() {
        vehiculos[nVeh++] = new Vehiculo("PVR-101", "Yaris", 28000);
        vehiculos[nVeh++] = new Vehiculo("PVR-202", "Accent", 30000);
        vehiculos[nVeh++] = new Vehiculo("PVR-303", "Rio", 27500);
        vehiculos[nVeh++] = new Vehiculo("PVR-404", "Versa", 29000);
    }

//Elegir el vehículo y asociar cliente
    static void elegirVehiculoYAsociarCliente() {
        System.out.println("Vehículos DISPONIBLES:");
        listarVehiculos(true);

        System.out.print("Placa: ");
        String pla = sc.nextLine().trim();
        int iv = buscarVehiculo(pla);
        if (iv == -1) { 
            System.out.println("No existe"); 
            return; 
        }

        Vehiculo v = vehiculos[iv];
        if (!v.disponible || v.mantenimiento) { 
            System.out.println("No disponible"); 
            return; 
        }

        System.out.print("Cédula del cliente: ");
        String ced = sc.nextLine().trim();
        if (buscarCliente(ced) == -1) { 
            System.out.println("Cliente no registrado"); 
            return; 
        }

        System.out.print("Días: ");
        int d = leerEntero();
        if (d <= 0) { 
            System.out.println("Días inválidos"); 
            return; 
        }

        v.reservadoPor = ced;
        v.diasReservados = d;
        v.disponible = false;
        System.out.println("Reserva creada para " + ced + " por " + d + " día(s).");
    }

}


