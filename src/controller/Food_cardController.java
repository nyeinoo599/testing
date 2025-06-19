package controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.foods;

public class Food_cardController {

    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Button editBtn;

    private foods food;
    private FoodController foodController;

    public void setData(foods food) {
        this.food = food;
        nameLabel.setText(food.getName());
        priceLabel.setText("$" + food.getPrice());

          try {
            String imagePath = food.getImagePath(); // This is the path from your database
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath); // Create a File object from the path

                // *** THIS IS THE MOST IMPORTANT PART TO FIX THE IMAGE DISPLAY ***
                if (file.exists()) { // Check if the file actually exists on the disk
                    String imageUrl = file.toURI().toString(); // Convert the File object to a proper file: URL string
                    System.out.println("Attempting to load image from URL: " + imageUrl); // For debugging
                    imageView.setImage(new Image(imageUrl, true)); // Load the image using the URL
                } else {
                    System.err.println("Image file not found: " + imagePath); // Log if the file doesn't exist
                    imageView.setImage(null); // Clear the ImageView or set a placeholder
                }
            } else {
                imageView.setImage(null); // Clear if no path is provided
            }
        } catch (Exception e) {
            System.err.println("Error loading image for " + food.getName() + " from path: " + food.getImagePath());
            e.printStackTrace(); // Print the full stack trace for detailed errors
            imageView.setImage(null); // Clear on any loading error
        }
    }
    

    public void setFoodController(FoodController controller) {
        this.foodController = controller;
        editBtn.setOnAction(e -> {
            foodController.openForm(food);
        });
    }
}