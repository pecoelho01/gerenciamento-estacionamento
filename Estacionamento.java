import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Estacionamento {
    private List<Carro> carPark = new ArrayList<>();
    private static final File BD_FILE = new File("baseDados.txt");
    private int capacity; 
    private LocalDateTime date;

    public Estacionamento() {
        if ( BD_FILE.exists() ){
            loadBD();
        }
    }

    public File getBD() {
        return BD_FILE;
    }

    public List<Carro> getCarsParking() {
        return carPark;
    }

    public int putCar(Carro e) {
        if ( ! carPark.contains(e)) {
            carPark.add(e);
            return 0;
        }
        return 1;
    }

    public int pushCar(Carro e) {
        if ( carPark.contains(e)) {
            carPark.remove(e);
            return 0;
        }
        return 1;
    }

    public void writeDB() {
        try {
            PrintWriter bd = new PrintWriter(getBD());

            bd.println(" ========== Base de Dados ============");

            for ( Carro car: carPark) {
                bd.println(car.getOwnCar() + "-" + car.getMatricula() + "-" + car.getCategory() + "-" + car.getFormatoDatEntrada() + "-" + car.getFormatoDataSaida());
            }
            
            bd.close();

        } catch (Exception e) {
            System.out.println("Erro a escrever na base de dados");
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
            st.append(c);
        }

        return st.toString();
    }

    
}