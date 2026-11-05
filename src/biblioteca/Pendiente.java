package biblioteca;

public class Pendiente implements EstadoReserva {
    public String nombre() { return "Pendiente"; }
    public void confirmar(Reserva reserva) {
        throw new UnsupportedOperationException("Se implementa en la Estación 7.");
    }
    public void cancelar(Reserva reserva) {
        throw new UnsupportedOperationException("Se implementa en la Estación 7.");
    }
    public void finalizar(Reserva reserva) {
        throw new IllegalStateException("No se puede finalizar una reserva pendiente.");
    }
}