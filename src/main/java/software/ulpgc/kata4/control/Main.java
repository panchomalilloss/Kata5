package software.ulpgc.kata4.control;

import software.ulpgc.kata4.io.RemoteMovieLoader;
import software.ulpgc.kata4.model.Movie;
import software.ulpgc.kata4.persistence.MovieStore;
import software.ulpgc.kata4.persistence.SQLiteMovieStore;
import software.ulpgc.kata4.view.HistogramDisplay;
import software.ulpgc.kata4.viewmodel.Histogram;
import software.ulpgc.kata4.viewmodel.HistogramBuilder;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        MovieStore store = new SQLiteMovieStore("movies.db");

        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Carga en remoto en el SQLite");
        System.out.println("2. Mostrar el histograma");

        String option = scanner.nextLine();

        switch (option) {
            case "1" -> loadRemoteData(store);
            case "2" -> showHistogram(store);
            default -> System.out.println("Opcion no valida");
        }
    }

    private static void showHistogram(MovieStore store) throws SQLException {
        List<Movie> movies = store.loadAll();

        if (movies.isEmpty()) {
            System.out.println("No hay histograma cargado");
            return;
        }

        Histogram histogram =
                new HistogramBuilder(movies)
                        .build(movie -> decadeOf(movie.year()));

        SwingUtilities.invokeLater(
                () -> new HistogramDisplay().show(histogram)
        );
    }

    private static void loadRemoteData(MovieStore store) {
        List<Movie> movies =
                new RemoteMovieLoader(Main::fromTsv).loadAll();

        store.saveAll(movies);

        System.out.println(movies.size() + " cargadas en el SQLite");
    }

    private static int decadeOf(int year) {
        if (year < 0) return -1;
        return (year/10) *10;
    }

    private static Movie fromTsv(String s) {
        return fromTsv(s.split("\t"));
    }

    private static Movie fromTsv(String[] split) {
        return new Movie(split[2], toInt(split[5]), toInt(split[7]));
    }

    private static int toInt(String s) {
        if (s.equals("\\N")) return -1;
        return Integer.parseInt(s);
    }
}
