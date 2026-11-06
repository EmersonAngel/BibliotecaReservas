package biblioteca;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Sala> salas = List.of(
                new Sala("A", "Sala A", 2),
                new Sala("B", "Sala B", 4),
                new Sala("C", "Sala C", 6),
                new Sala("D", "Sala D", 8)
        );
        System.out.println("Salas de la biblioteca:");
        salas.forEach(System.out::println);

        Reserva reserva = new ReservaBuilder()
                .estudiante(new Estudiante("E001", "Laura"))
                .fecha(LocalDate.now().plusDays(7))
                .horaInicio(LocalTime.of(14, 0))
                .duracionHoras(2)
                .cantidadPersonas(4)
                .sala(new Sala("B", "Sala B", 4))
                .build();

        System.out.println(reserva);

        // Prueba negativa 1: reserva sin estudiante
        try {
            new ReservaBuilder()
                    .fecha(LocalDate.now().plusDays(7))
                    .horaInicio(LocalTime.of(14, 0))
                    .duracionHoras(2)
                    .cantidadPersonas(4)
                    .sala(new Sala("B", "Sala B", 4))
                    .build();
        } catch (IllegalStateException ex) {
            System.out.println("Prueba negativa 1 rechazada: " + ex.getMessage());
        }

        // Prueba negativa 2: sala sin capacidad suficiente
        try {
            new ReservaBuilder()
                    .estudiante(new Estudiante("E002", "Andrés"))
                    .fecha(LocalDate.now().plusDays(7))
                    .horaInicio(LocalTime.of(14, 0))
                    .duracionHoras(2)
                    .cantidadPersonas(6)
                    .sala(new Sala("A", "Sala A", 2))
                    .build();
        } catch (IllegalStateException ex) {
            System.out.println("Prueba negativa 2 rechazada: " + ex.getMessage());
        }

        List<Sala> disponibles = List.of(
                new Sala("A", "Sala A", 2),
                new Sala("B", "Sala B", 4),
                new Sala("C", "Sala C", 6),
                new Sala("D", "Sala D", 8)
        );

        EstrategiaSeleccionSala porCapacidad = new SeleccionPorCapacidad();
        System.out.println("Para 4 personas: "
                + porCapacidad.seleccionar(disponibles, 4).orElse(null));

        EstrategiaSeleccionSala preferida = new SeleccionSalaPreferida("C");
        System.out.println("Preferida C: "
                + preferida.seleccionar(disponibles, 4).orElse(null));

        EstrategiaSeleccionSala preferidaNoAdecuada = new SeleccionSalaPreferida("B");
        System.out.println("Preferida B para 7: "
                + preferidaNoAdecuada.seleccionar(disponibles, 7).orElse(null));

        System.out.println("\n=== PRUEBA OBSERVER ===");
        RepositorioSalasMemoria repoSalas = new RepositorioSalasMemoria();
        RepositorioReservasMemoria repoReservas = new RepositorioReservasMemoria();
        List<ObservadorReserva> observadores = List.of(
                new NotificadorEstudiante(),
                new RegistroEventos()
        );
        ReservaFacade fachada = new ReservaFacade(repoSalas, repoReservas, observadores);

        Reserva reservaObserver = fachada.realizarReserva(
                new Estudiante("E001", "Laura"),
                LocalDate.now().plusDays(7),
                LocalTime.of(14, 0),
                2,
                4,
                new SeleccionPorCapacidad()
        ).orElseThrow();

        System.out.println("Laura cancela la reserva:");
        reservaObserver.cancelar();

        System.out.println("\n=== PRUEBAS STATE ===");

// 1. Confirmar una reserva pendiente
        Reserva reservaSofia = new ReservaBuilder()
                .estudiante(new Estudiante("E010", "Sofía"))
                .fecha(LocalDate.now().plusDays(3))
                .horaInicio(LocalTime.of(9, 0))
                .duracionHoras(1)
                .cantidadPersonas(2)
                .sala(new Sala("A", "Sala A", 2))
                .build();
        observadores.forEach(reservaSofia::agregarObservador);
        System.out.println("1. Estado inicial: " + reservaSofia.getNombreEstado());
        reservaSofia.confirmar();
        System.out.println("1. Después de confirmar: " + reservaSofia.getNombreEstado());

// 2. Cancelar una reserva confirmada
        reservaSofia.cancelar();
        System.out.println("2. Después de cancelar: " + reservaSofia.getNombreEstado());

// 3. Intentar confirmar una reserva cancelada
        try {
            reservaSofia.confirmar();
        } catch (IllegalStateException ex) {
            System.out.println("3. Rechazado: " + ex.getMessage()
                    + " Estado: " + reservaSofia.getNombreEstado());
        }

// 4. Finalizar otra reserva confirmada
        Reserva reservaSamuel = fachada.realizarReserva(
                new Estudiante("E004", "Samuel"),
                LocalDate.now().plusDays(10),
                LocalTime.of(11, 0),
                1,
                2,
                new SeleccionPorCapacidad()
        ).orElseThrow();
        reservaSamuel.finalizar();
        System.out.println("4. Después de finalizar: " + reservaSamuel.getNombreEstado());

// 5. Intentar cancelar una reserva finalizada
        try {
            reservaSamuel.cancelar();
        } catch (IllegalStateException ex) {
            System.out.println("5. Rechazado: " + ex.getMessage()
                    + " Estado: " + reservaSamuel.getNombreEstado());
        }
    }
}