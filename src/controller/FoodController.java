package controller;

import database.DbConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.foods;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class FoodController implements Initializable {

    @FXML
    private FlowPane cardContainer;
    Connection con;

    private List<foods> foodList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFromDatabase();
        showFoodCards();
    }

    // Load food items from DB into foodList
    private void loadFromDatabase() {
        foodList.clear();
        String query = "SELECT food_id, food_name, price, img FROM food";

        try (
              Connection con = DbConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                foods food = new foods(
                        rs.getInt("food_id"),
                        rs.getString("food_name"),
                        rs.getString("price"),
                        rs.getString("img")
                );
                foodList.add(food);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show all food cards in the FlowPane
    private void showFoodCards() {
        cardContainer.getChildren().clear();
        try {
            for (foods food : foodList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/food_card.fxml"));
                AnchorPane card = loader.load();

                Food_cardController cardController = loader.getController();
                cardController.setData(food);
                cardController.setFoodController(this); // To handle edit button action

                cardContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open Add/Edit form, pass food to edit or null for new
    public void openForm(foods foodToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_food.fxml"));
            Parent root = loader.load();

            Add_foodController controller = loader.getController();
            if (foodToEdit != null) {
                controller.setEditMode(true);
                controller.setFood(foodToEdit);
            } else {
                controller.setEditMode(false);
            }

            Stage stage = new Stage();
            stage.setTitle(foodToEdit == null ? "Add New Food" : "Edit Food");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            foods updatedFood = controller.getUpdatedFood();
            if (updatedFood != null) {
                if (foodToEdit == null) {
                    // New food added
                    foodList.add(updatedFood);
                } else {
                    // Edited food - update list
                    int idx = -1;
                    for (int i = 0; i < foodList.size(); i++) {
                        if (foodList.get(i).getId() == updatedFood.getId()) {
                            idx = i;
                            break;
                        }
                    }
                    if (idx != -1) {
                        foodList.set(idx, updatedFood);
                    }
                }
                showFoodCards(); // Refresh UI cards
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButtonClick() {
        openForm(null);
    }
}