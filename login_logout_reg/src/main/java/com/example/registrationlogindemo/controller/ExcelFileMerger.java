package com.example.registrationlogindemo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class ExcelFileMerger {
    public static void main(String[] args) {
        String inputFolderPath = "C:\\Users\\DINESH KUMAR\\Desktop\\vishh"; // Replace with your folder path
        String outputCsvFilePath = "C:\\Users\\DINESH KUMAR\\Desktop\\vishh\\merged.csv"; // Output CSV file in the same folder

        try {
            mergeCsvFiles(inputFolderPath, outputCsvFilePath);
            System.out.println("CSV files merged successfully: " + outputCsvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mergeCsvFiles(String folderPath, String outputCsvFilePath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsvFilePath, true))) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] row = line.split(",");
                            writer.writeNext(row);
                        }
                    }
                }
            }
        }
    }
}

 