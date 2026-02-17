package com.pecoelhoo.estacionamento_api.service;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pecoelhoo.estacionamento_api.model.Carro;

@Service
public class Estacionamento {
    private List<Carro> carPark = new ArrayList<>();
    private List<Carro> archivePark = new ArrayList<>();
    private final File dataDir;
    private final File bdFile;
    private final File archFile;
    private int capacity; 
    private LocalDateTime date;

    public Estacionamento(@Value("${app.data.dir}") String path) {
        this.dataDir = new File(path);
        this.bdFile = new File(dataDir, "baseDados.txt");
        this.archFile = new File(dataDir, "archiveCars.txt");
        ensureStorageReady();
        if ( bdFile.length() > 0 ){
            loadBD();
        }
    }

    private void ensureStorageReady() {
        try {
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                throw new IllegalStateException("Não foi possível criar diretório de dados: " + dataDir.getAbsolutePath());
            }
            if (!bdFile.exists()) {
                writeDB();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao preparar armazenamento: " + e.getMessage(), e);
        }
    }

    public File getBD() {
        return bdFile;
    }

    public File getArchive() {
        return archFile;
    }

    public List<Carro> getCarsParking() {
        return carPark;
    }

    public int putCar(Carro e) {
        for ( Carro c: carPark) {
            if ( c.getMatricula().equals(e.getMatricula())) {
                   return 1;
            }
        }
        carPark.add(e);
        return 0;
    }

    public int pushCar(Carro e) {
        if ( carPark.contains(e)) {
            archivePark.add(e);
            carPark.remove(e);
            return 0;
        }
        return 1;
    }

    public void writeDB() {
        try (PrintWriter bd = new PrintWriter(getBD())) {

            bd.println(" ========== Base de Dados ============");

            for ( Carro car: carPark) {
                bd.println(car.getOwnCar() + "-" + car.getMatricula() + "-" + car.getCategory() + "-" + car.getFormatoDatEntrada() + "-" + car.getFormatoDataSaida());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Erro a escrever na BD: " + e.getMessage(), e);
        }
    }

    public void writeArchive() {
        try (PrintWriter archive = new PrintWriter(getArchive())) {

            archive.println("========== Arquivo da Base de Dados ==========");
            for ( Carro car: archivePark) {
                archive.println(car.getOwnCar() + "-" + car.getMatricula() + "-" + car.getCategory() + "-" + car.getFormatoDatEntrada() + "-" + car.getFormatoDataSaida());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Erro a escrever no Arquivo do sistema: " + e.getMessage(), e);
        }
    }

    public void loadBD() {
        try {
            Scanner sc = new Scanner(getBD());
            sc.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH'h'mm");

            while( sc.hasNextLine() ) {
                String line = sc.nextLine();
                String [] linhaElementos = line.split("-");

                String nameMotorista = linhaElementos[0];
                String matricula = linhaElementos[1];
                char categoria = linhaElementos[2].charAt(0);
                LocalDateTime dataEntrada = "null".equals(linhaElementos[3]) ? null : LocalDateTime.parse(linhaElementos[3], formatter);
                LocalDateTime dataSaida = "null".equals(linhaElementos[4]) ? null : LocalDateTime.parse(linhaElementos[4], formatter);


                Carro e = new Carro(nameMotorista, matricula, categoria, dataEntrada, dataSaida);

                carPark.add(e);
            }

            sc.close();

            
        } catch (Exception e) {
            System.out.println("Erro a carregar os dados");
        }
    }
    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();

        st.append(" ============= Estes são os carros estacionaos =============" + "\n");

        for ( Carro c: carPark){
            st.append(c + "\n");
        }

        return st.toString();
    }

    
}
