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
    }
}