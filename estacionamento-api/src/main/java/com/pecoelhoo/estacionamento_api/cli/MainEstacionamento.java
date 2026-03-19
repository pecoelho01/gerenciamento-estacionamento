package com.pecoelhoo.estacionamento_api.cli;

import java.time.LocalDateTime;
import java.util.Scanner;

import com.pecoelhoo.estacionamento_api.model.Carro;
import com.pecoelhoo.estacionamento_api.service.Estacionamento;

public class MainEstacionamento {
    public static void main(String[] args) {
        String dataDir = System.getenv().getOrDefault("DATA_DIR", "./data");
        Estacionamento est = new Estacionamento(dataDir);
        Scanner sc = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n===== MENU ESTACIONAMENTO =====");
            System.out.println("1 - Adicionar carro");
            System.out.println("2 - Retirar carro");
            System.out.println("3 - Listar carros estacionados");
            System.out.println("4 - Guardar base de dados");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            
            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1 -> {
                    System.out.print("Nome do motorista: ");
                    String nome = sc.nextLine();

                    System.out.print("Matrícula (6 caracteres): ");
                    String mat = sc.nextLine();

                    System.out.print("Categoria (L/P/M): ");
                    String categoriaInput = sc.nextLine().trim().toUpperCase();
                    char cat = categoriaInput.isEmpty() ? ' ' : categoriaInput.charAt(0);

                    LocalDateTime entrada = LocalDateTime.now();

                    try {
                        Carro novo = new Carro(nome, mat, cat, entrada, null);
                        if (est.putCar(novo) == 0)
                            System.out.println("✅ Carro adicionado com sucesso!");
                        else
                            System.out.println("⚠️ Carro já está no estacionamento!");
                    } catch (Exception e) {
                        System.out.println("❌ Erro ao criar carro: " + e.getMessage());
                    }
                }

                case 2 -> {
                    System.out.print("Matrícula do carro a remover: ");
                    String mat = sc.nextLine();

                    Carro remover = null;
                    for (Carro c : est.getCarsParking()) {
                        if (c.getMatricula().equalsIgnoreCase(mat)) {
                            remover = c;
                            break;
                        }
                    }

                    LocalDateTime saidaData = LocalDateTime.now();
                    if (remover != null) {
                        est.pushCar(remover, saidaData);
                        System.out.println("🚗 Carro removido com sucesso!");
                    } else {
                        System.out.println("❌ Carro não encontrado!");
                    }
                }

                case 3 -> {
                    System.out.println("\n" + est);
                }

                case 4 -> {
                    est.writeDB();
                    System.out.println("💾 Base de dados gravada!");
                }

                case 0 -> {
                    System.out.println("👋 A sair...");
                    est.writeDB();
                }

                default -> {
                    if (opcao != 0)
                        System.out.println("❗ Opção inválida. Tente novamente.");
                }
            }
        }

        sc.close();
    }
}
