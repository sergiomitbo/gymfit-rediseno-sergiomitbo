package es.medac.sergiomitbo.gymfit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Socio {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String provincia;
    private String cuota;
    private List<String> clases;
    private String iban;

    public Socio(String nombre, String apellidos, LocalDate fechaNacimiento, String sexo,
                 String provincia, String cuota, List<String> clases, String iban) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.provincia = provincia;
        this.cuota = cuota;
        this.clases = new ArrayList<>(clases);
        this.iban = iban;
    }

    // Getters que usa la tabla (PropertyValueFactory)
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public String getFechaTexto() {
        return fechaNacimiento.format(FORMATO);
    }

    public String getCuota() {
        return cuota;
    }

    public String getProvincia() {
        return provincia == null ? "—" : provincia;
    }

    public String getClasesTexto() {
        return clases.isEmpty() ? "—" : String.join(", ", clases);
    }

    // Getters normales
    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public List<String> getClases() {
        return clases;
    }

    public String getIban() {
        return iban;
    }
}
