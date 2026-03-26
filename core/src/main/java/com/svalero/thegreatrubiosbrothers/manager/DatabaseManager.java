package com.svalero.thegreatrubiosbrothers.manager;

import com.badlogic.gdx.Gdx;
import com.svalero.thegreatrubiosbrothers.model.Score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    //Guardar la puntuación
    public static void saveScore(String name, int score) {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = null;
            // Uso .local() para que nos deje escribir y guardar la partida sin errores
            connection = DriverManager.getConnection("jdbc:sqlite:" + Gdx.files.local("scores.db").path());

            //Crea tabla si no existe
            String sql = "CREATE TABLE IF NOT EXISTS scores (id integer primary key autoincrement, name text, score int)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            //Inserta los datos
            sql = "INSERT INTO scores (name, score) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, score);
            statement.executeUpdate();

            //Cierra conexiones
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    // Leer el TOP 10
    public static List<Score> getTopScores() {
        List<Score> scores = new ArrayList<Score>();

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:sqlite:" + Gdx.files.local("scores.db").path());

            // Consultar los 10 mejores ordenados de mayor a menor
            String sql = "SELECT name, score FROM scores ORDER BY score DESC LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            Score score;
            while (result.next()) {
                score = new Score();
                score.name = result.getString("name");
                score.score = result.getInt("score");
                scores.add(score);
            }

            // Cerrar conexiones
            if (statement != null) statement.close();
            if (result != null) result.close();
            if (connection != null) connection.close();

            return scores;

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return new ArrayList<Score>();
    }
}
