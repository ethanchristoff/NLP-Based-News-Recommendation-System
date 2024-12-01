package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class recommend extends fundamental_tools {
    //------Variable Initializers------
    private List<Runnable> tasks = new ArrayList<>();
    //------SQL Based Variables------
    private ResultSet rs;
    private String sql;
    private PreparedStatement pstmt;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------View_Recommended_Articles Functions------
    public void set_preferred_genre(String preferred_genre, String username) {
        String sql = "UPDATE users SET Preferred_Genres = ? WHERE username = ?";
        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();
        try {
            tasks.add(()-> {
                try {
                    pstmt.setString(1, preferred_genre);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tasks.add(()-> {
                try {
                    pstmt.setString(2, username);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            run_simultaneously(tasks);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }

    public String[] getPreferredGenreArray(String summarized_genre){
        String[] preferred_genre = switch (summarized_genre.toLowerCase()) {
            case "society & culture" -> new String[]{
                    "LATINO VOICES", "BLACK VOICES", "QUEER VOICES", "STYLE & BEAUTY",
                    "STYLE", "CULTURE & ARTS", "ARTS", "ARTS & CULTURE",
                    "PARENTING", "PARENTS", "WEDDINGS", "DIVORCE",
                    "WOMEN", "GREEN", "ENVIRONMENT"
            };
            case "news & current events" -> new String[]{
                    "WORLDPOST", "U.S. NEWS", "WORLD NEWS", "POLITICS",
                    "CRIME", "MEDIA", "EDUCATION", "TECH", "SCIENCE"
            };
            case "lifestyle & well-being" -> new String[]{
                    "HEALTHY LIVING", "WELLNESS", "HOME & LIVING", "GOOD NEWS",
                    "FOOD & DRINK", "TASTE", "TRAVEL", "IMPACT"
            };
            case "entertainment & leisure" -> new String[]{
                    "COMEDY", "WEIRD NEWS", "ENTERTAINMENT", "SPORTS",
                    "BUSINESS", "MONEY", "COLLEGE", "FIFTY", "RELIGION"
            };
            default -> new String[]{};
        };
        return preferred_genre;
    }

    public String get_preferred_genre(String username) {
        String[] sessionData = readSessionCredentials(username);

        sql = "SELECT Preferred_Genres  FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        try {

            pstmt.setString(1, sessionData[0]);
            pstmt.setString(2, sessionData[1]);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Preferred_Genres");
            } else {
                showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
        return null;
    }
}
