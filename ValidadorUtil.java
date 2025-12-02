package com.classes.util;

import java.util.regex.Pattern;

public class ValidadorUtil {

    private static final String NOME_REGEX = "^[a-zA-ZÀ-ÿ\\s]{3,}$";
    private static final Pattern NOME_PATTERN = Pattern.compile(NOME_REGEX);

    public static boolean isValidNome(String nome) {
        if (nome == null || nome.trim().length() < 3) {
            return false;
        }
        return NOME_PATTERN.matcher(nome.trim()).matches();
    }

    public static boolean isValidCpf(String cpfApenasNumeros) {
        if (cpfApenasNumeros == null) return false;
        // Verifica se tem exatamente 11 números
        return cpfApenasNumeros.matches("\\d{11}");
    }

    // --- NOVO MÉTODO PARA CNPJ ---
    public static boolean isValidCnpj(String cnpjApenasNumeros) {
        if (cnpjApenasNumeros == null) return false;
        // Verifica se tem exatamente 14 números
        return cnpjApenasNumeros.matches("\\d{14}");
    }

    public static boolean isValidTelefone(String telefoneApenasNumeros) {
        if (telefoneApenasNumeros == null) return false;
        return telefoneApenasNumeros.matches("\\d{10,11}");
    }
}