
package com.pecoelhoo.estacionamento_api.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Carro {
    private String ownCar;
    private String matricula;
    private char category;
    private LocalDateTime localEntrada;
    private LocalDateTime localSaida;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH'h'mm");

    public Carro(String ownCar, String matricula, char category, LocalDateTime localEntrada, LocalDateTime localSaida) throws Exception{
        this.ownCar = ownCar;
        if ( checkMatricula(matricula) != 0 ) {
            throw new Exception("Formato de matrícula inválido. Precisa de ter 6 caracteres. Este deve ser o formato: XXAAYY");
        }
        this.matricula = matricula;

        char normalizedCategory = normalizeCategory(category);
        if ( checkCategory(normalizedCategory) != 0) {
            throw new Exception("Formato da categoria do carro inválida. Precisa de ser: L, P ou M");
        }
        this.category = normalizedCategory;
        this.localEntrada = localEntrada;
        this.localSaida = localSaida;
    }

    public String getOwnCar(){
        return ownCar;
    }

    public String getMatricula(){
        return matricula;
    }

    public char getCategory(){
        return category;
    }

    public LocalDateTime getDataEntrada() {
        return localEntrada;
    }

    public LocalDateTime getDataSaida() {
        return localSaida;
    }

    public String getFormatoDatEntrada() {
          if( getDataEntrada() == null ) {
             return null;
          }

          return localEntrada.format(FORMAT);
    }

    public String getFormatoDataSaida() {
          if( getDataSaida() == null ) {
             return null;
          }

          return localSaida.format(FORMAT);
    }


    public int checkMatricula(String mat) {
        if ( mat.length() != 6 ) {
            return 1;
        }
        return 0;
    }

    public int checkCategory(char category) {
        if ( category != 'L' && category != 'P' && category != 'M') {
            return 1;
        }
        return 0;
    }

    private char normalizeCategory(char category) {
        return Character.toUpperCase(category);
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();

        st.append("Motorista: " + getOwnCar() + " " 
        + "Matrícula: " + getMatricula() + " " 
        + "Categoria: " + getCategory() + " "
        + "Entrada: " + getFormatoDatEntrada() + " "
        + "Saída: " + getFormatoDataSaida());

        return st.toString();
    }

}
