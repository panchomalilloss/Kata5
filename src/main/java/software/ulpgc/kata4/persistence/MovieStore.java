package software.ulpgc.kata4.persistence;

import software.ulpgc.kata4.model.Movie;

import java.sql.SQLException;
import java.util.List;

public interface MovieStore {

    void saveAll(List<Movie> movies);
    List<Movie> loadAll() throws SQLException;

}
