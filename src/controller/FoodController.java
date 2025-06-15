/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import database.DbConnection;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author xenon
 */
public class FoodController implements Initializable {

    @FXML
    private FlowPane cardContainer;
    @FXML
    private Button addCardButton;
    @FXML
    private Button card;
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
     public void initialize(URL url, ResourceBundle rb) {
        DbConnection db=new DbConnection();
        try {
            con = db.getConnection();
            loadAllProducts();
        } catch (ClassNotFoundException | SQLException ex) {
            System.getLogger(MainController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }  
     
     private void loadAllProducts() throws SQLException {
        
        String sql = "Select * from food";
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();
        cardContainer.getChildren().clear();
            while (rs.next()) {
                String name = rs.getString("food_name");
                String price = rs.getString("price");
                String imagePath = rs.getString("img");

                VBox card = createCard(name, price, imagePath);
                cardContainer.getChildren().add(card);
            }
        
    }
     
      private VBox createCard(String name, String price, String imagePath) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("""
        -fx-font-weight: 600;
        -fx-font-size: 16px;
        -fx-text-fill: #2c3e50;
        -fx-wrap-text: true;
    """);

        
        Label priceLabel = new Label("" + price);
    priceLabel.setStyle("""
        -fx-font-weight: bold;
        -fx-font-size: 14px;
        -fx-text-fill: white;
        -fx-background-color: #27ae60;
        -fx-padding: 5 12 5 12;
        -fx-background-radius: 15;
        -fx-alignment: center;
    """);

        ImageView imageView = new ImageView();
        try {
        File file = new File(imagePath); // dynamic path
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
        } else {
            System.out.println("Image not found: " + imagePath);
        }
    } catch (Exception e) {
        imageView.setImage(null);
    }
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        VBox card = new VBox(10, imageView, nameLabel, priceLabel);
        card.setPadding(new Insets(15));
    card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);
        -fx-alignment: center;
    """);
        card.setPrefSize(160, 200);
        card.setOnMouseEntered(e -> card.setStyle("""
        -fx-background-color: #f0f5f9;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(three-pass-box, rgba(39, 174, 96, 0.35), 15, 0, 0, 6);
        -fx-alignment: center;
    """));
        
        card.setOnMouseExited(e -> card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);
        -fx-alignment: center;
    """));
        
        card.setOnMouseClicked(event -> openEditDialog(name, price, imagePath));
        
        card.setOnMouseEntered(e -> card.setStyle("""
        -fx-background-color: #f0f5f9;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(three-pass-box, rgba(39, 174, 96, 0.35), 15, 0, 0, 6);
        -fx-alignment: center;
    """));
        
        card.setOnMouseExited(e -> card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);
        -fx-alignment: center;
    """));
        
        
        return card;
    }
    
      
       @FXML
    void handleAddCard(ActionEvent event) {
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Product");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f7f9fc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label nameLabel = new Label("Product Name");
        nameLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter product name");
        nameField.setPrefWidth(300);
        nameField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #ccc;");
        
        // Price field
        Label priceLabel = new Label("Price");
        priceLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price (e.g. 12.99)");
        priceField.setPrefWidth(300);
        priceField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #ccc;");
        
        
        Label imageLabel = new Label("Product Image");
        imageLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        Label imagePathLabel = new Label("No file chosen");
        imagePathLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
        
        
        Button saveButton = new Button("Save");

        final String[] imagePath = new String[1];

        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {
                imagePath[0] = file.getAbsolutePath();
                imagePathLabel.setText(file.getName());
            }
        });

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String priceText = priceField.getText();

            if (name.isEmpty() || priceText.isEmpty() || imagePath[0] == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill all fields.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            try {
                String sql = "INSERT INTO food (food_name, price, img) VALUES (?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, priceText);
                pst.setString(3, imagePath[0]);
                pst.executeUpdate();

                dialogStage.close();
                loadAllProducts();
                nameField.clear();
                priceField.clear();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR, "Error saving product.", ButtonType.OK);
                error.showAndWait();
            }
        });

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Price:"), priceField);
        grid.addRow(2, new Label("Image:"), chooseImageButton, imagePathLabel);
        grid.add(saveButton, 1, 3);

        Scene scene = new Scene(grid);
        dialogStage.setScene(scene);
        dialogStage.show();

    }
   
    
    @FXML
    private void openEditDialog(String oldName, String oldPrice, String oldImagePath) {
    Stage dialogStage = new Stage();
    dialogStage.setTitle("Edit Product");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(20));
    grid.setVgap(15);
    grid.setHgap(15);
    grid.setStyle("-fx-background-color: #f7f9fc; -fx-border-radius: 10; -fx-background-radius: 10;");

    Label nameLabel = new Label("Product Name");
    nameLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
    TextField nameField = new TextField(oldName);
    nameField.setPrefWidth(300);
    nameField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #ccc;");

    Label priceLabel = new Label("Price");
    priceLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
    TextField priceField = new TextField(oldPrice);
    priceField.setPrefWidth(300);
    priceField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #ccc;");

    Label imageLabel = new Label("Product Image");
    imageLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");
    Button chooseImageButton = new Button("Choose Image");
    chooseImageButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
    Label imagePathLabel = new Label(new File(oldImagePath).getName());
    imagePathLabel.setStyle("-fx-text-fill: #333;");

    final String[] imagePath = new String[] { oldImagePath };
    chooseImageButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            imagePath[0] = file.getAbsolutePath();
            imagePathLabel.setText(file.getName());
            imagePathLabel.setStyle("-fx-text-fill: #333;");
        }
    });

    Button saveButton = new Button("Update Product");
    saveButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
    saveButton.setPrefWidth(150);

    saveButton.setOnAction(e -> {
        String newName = nameField.getText().trim();
        String newPrice = priceField.getText().trim();

        if (newName.isEmpty() || newPrice.isEmpty() || imagePath[0] == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill all fields.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Update statement - assuming 'food_name' is unique (or add your unique id)
            String sql = "UPDATE food SET food_name=?, price=?, img=? WHERE food_name=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newName);
            pst.setString(2, newPrice);
            pst.setString(3, imagePath[0]);
            pst.setString(4, oldName);
            int updated = pst.executeUpdate();

            if (updated > 0) {
                dialogStage.close();
                loadAllProducts();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Update failed. Product not found.", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "Error updating product.", ButtonType.OK);
            error.showAndWait();
        }
    });

    grid.add(nameLabel, 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(priceLabel, 0, 1);
    grid.add(priceField, 1, 1);
    grid.add(imageLabel, 0, 2);
    grid.add(chooseImageButton, 1, 2);
    grid.add(imagePathLabel, 1, 3);
    grid.add(saveButton, 1, 4);
    GridPane.setMargin(saveButton, new Insets(15, 0, 0, 0));

    Scene scene = new Scene(grid);
    dialogStage.setScene(scene);
    dialogStage.initOwner(addCardButton.getScene().getWindow());
    dialogStage.setResizable(false);
    dialogStage.show();
}

        
    
}
