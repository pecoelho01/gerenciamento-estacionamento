package com.pecoelhoo.estacionamento_api.service;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pecoelhoo.estacionamento_api.model.Carro;

@Service
public class Estacionamento {
    private static final Logger log = LoggerFactory.getLogger(Estacionamento.class);
    private List<Carro> carPark = new ArrayList<>();
    private List<Carro> archivePark = new ArrayList<>();
    private final File dataDir;
    private final File bdFile;
    private final File archFile;
    private final int CAPACITY = 50; 
    private int count;
    private LocalDateTime date;

    public Estacionamento(@Value("${app.data.dir}") String path) {
        this.dataDir = new File(path);
        this.bdFile = new File(dataDir, "baseDados.txt");
        this.archFile = new File(dataDir, "archiveCars.txt");
        ensureStorageReady();
        logStorageConfiguration();
        if ( bdFile.length() > 0 ){
            loadBD();
        }
    }

    private void logStorageConfiguration() {
        log.info("Diretório de dados configurado em: {}", dataDir.getAbsolutePath());
        log.info("Ficheiro base de dados: {}", bdFile.getAbsolutePath());
        log.info("Ficheiro de arquivo: {}", archFile.getAbsolutePath());

        if (System.getenv("RENDER") != null && !dataDir.getAbsolutePath().startsWith("/var/data")) {
            log.warn("Render detetado sem DATA_DIR em /var/data. Sem disco persistente os dados podem ser perdidos em restart.");
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

    public int getParkCars() {
        return count;
    }

    public int putCar(Carro e) {
        if ( count == CAPACITY) {
            return 1;
        }

        for ( Carro c: carPark) {
            if ( c.getMatricula().equals(e.getMatricula())) {
                   return 1;
            }
        }
        carPark.add(e);
        count++;


        return 0;
    }

    public int pushCar(Carro e, LocalDateTime date) {
        if ( carPark.contains(e)) {
            archivePark.add(e);
            e.setFormatoDataSaida(date);
            writeArchive();
            carPark.remove(e);
            count--;
            return 0;
        }
        return 1;
    }

    public void writeDB() {
        try (PrintWriter bd = new PrintWriter(getBD())) {

            bd.println(" ========== Base de Dados ============");

            bd.println("Lotação: " + count + "/" + CAPACITY);
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
            String countNotFinal = sc.nextLine();
            String [] countElements = countNotFinal.split(" ");
            String [] countFinal1 = countElements[1].split("/");
            int countFinal = Integer.parseInt(countFinal1[0]);

            count = countFinal;

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
        st.append("Lotação: " + getParkCars() + "/50");
        for ( Carro c: carPark){
            st.append(c + "\n");
        }

        return st.toString();
    }

    
}
