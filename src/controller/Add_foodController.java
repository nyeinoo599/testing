package controller;

import database.DbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.foods;
import java.io.File; // 
import javafx.stage.FileChooser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import database.DbConnection;


import database.DbConnection;

public class Add_foodController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField imagePathField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button uploadBtn;
    
    Connection con;

    private boolean editMode = false;
    private foods food;  // existing food if editing
    private foods updatedFood = null;

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void setFood(foods food) {
        this.food = food;
        if (food != null) {
            nameField.setText(food.getName());
            priceField.setText(food.getPrice());
            imagePathField.setText(food.getImagePath());
        }
    }

    @FXML
    private void initialize() {
        saveBtn.setOnAction(e -> saveFood());
        cancelBtn.setOnAction(e -> closeWindow());
    }
    @FXML 
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        
         File initialDirectory = new File(System.getProperty("user.home"));
         if (initialDirectory.exists() && initialDirectory.isDirectory()) {
             fileChooser.setInitialDirectory(initialDirectory);
         }

        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage currentStage = (Stage) uploadBtn.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void saveFood() {
        String name = nameField.getText();
        String price = priceField.getText();
        String imagePath = imagePathField.getText();

        try (
            Connection con = DbConnection.getConnection()) {
            if (editMode && food != null) {
                String sql = "UPDATE food SET food_name=?, price=?, img=? WHERE food_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, price);
                ps.setString(3, imagePath);
                ps.setInt(4, food.getId());
                ps.executeUpdate();

                food.setName(name);
                food.setPrice(price);
                food.setImagePath(imagePath);
                updatedFood = food;
            } else {
                String sql = "INSERT INTO food(food_name, price, img) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, name);
                ps.setString(2, price);
                ps.setString(3, imagePath);
                ps.executeUpdate();

                var keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    updatedFood = new foods(id, name, price, imagePath);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    public foods getUpdatedFood() {
        return updatedFood;
    }
}