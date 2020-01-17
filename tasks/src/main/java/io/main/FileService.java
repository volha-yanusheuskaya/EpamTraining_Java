package io.main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FileService {
  private static final String ALL_EXPANSION = ".+(\\.[a-z0-9]{2,4})$";

  private static List<String> listOfLines = new ArrayList<>();
  private static List<String> listOfFiles = new ArrayList<>();

  private static int countFiles = 0;
  private static int countFolders = -1; //not count the main folder

  private static void getListOfLines(String lineNext) {
    listOfLines.add(lineNext.replace("|", " ").replace("-", " ").trim());
    listOfLines.removeIf(line -> (line.length() == 0)); //remove empty lines
  }

  private static void getListOfFiles() {
    for (String lines : listOfLines) {
      if (lines.matches(ALL_EXPANSION)) {  //find all expansion
        listOfFiles.add(lines);
        countFiles++;
      } else {
        countFolders++;
      }
    }
  }

  private static int calculateAverageQtyOfFilesInFolder() {
    return countFiles / countFolders;
  }

  private static int calculateAverageLengthOfFileNames() {
    int nameFiles;
    int lengthNameFiles = 0;
    for (String listFile : listOfFiles) {
      nameFiles = listFile.lastIndexOf('.');
      lengthNameFiles += nameFiles;
    }
    return lengthNameFiles / listOfFiles.size();
  }

  static void getInfoAboutFile(Path path) throws IOException {
    try (FileReader reader = new FileReader(path.toFile())) {
      Scanner sc = new Scanner(reader);
      while (sc.hasNext()) {
        getListOfLines(sc.nextLine());
      }
      if (!listOfLines.isEmpty()) {
        getListOfFiles();
        System.out.println(
            String.format("Count files : %d %nCount folders : %d", countFiles, countFolders));
        System.out.println(String
            .format("Average files in folder : %d %nAverage file name : %d ", calculateAverageQtyOfFilesInFolder(),
                calculateAverageLengthOfFileNames()));
      } else {
        throw new IllegalArgumentException("File is empty!");
      }
    }
  }

  static void getFileTree(Path path) throws IOException {
    FileTreeVisitor fileTreeVisitor = new FileTreeVisitor();
    Files.walkFileTree(path, fileTreeVisitor);
    String fileVisitResult = fileTreeVisitor.toString();
    try (FileWriter fileWriter = new FileWriter("data/files.txt")) {
      fileWriter.write(fileVisitResult);
    }
  }

}
