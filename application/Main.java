package application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.event.EventHandler;


/**
 * Filename:   Main.java
 * Project:    p5
 * Course:     cs400 
 * Authors:   Vinnie, David, Charles, Monica 
 * Due Date:  10/12 
 * 
 *
 * Additional credits:
 *
 * Bugs or other notes: List does not sort correctly if item is add before a list is loaded. Also adds 2 to 3 times. Filtered food can't use meal summary
 *
 */
public class Main extends Application {
    private FoodData food = new FoodData(); // Creates a new instance of food data so we can use the food items
    private Scene loadScene, homeScene, addFoodScene, mealListScene, mealSummaryScene; // Different scenes for the GUI
    private List<FoodItem> foodList = new ArrayList<FoodItem>();//Holds the food that we want in the meal
    private List<FoodItem> mealList = new ArrayList<FoodItem>();//Holds the food that we want in the meal
    private List<FoodItem> filteredFoodList = new ArrayList<FoodItem>(); //creates a new list for the filtered foods
    private ListView<String> filteredListView;
    private ListView<String> listView;
    private int filterCount = 0;
    private Button addToMeal;
    private Button saveButton;
    private ArrayList<String> choosenFoods = new ArrayList<String>();
    private ObservableList<String> items;
    private ListView nutrientsList = new ListView();
    private int nutrientsListSize = 0;
    private ListView comparisonsList = new ListView();
    private int comparisonsListSize = 0;
    private ListView inputList = new ListView();
    private Text foodItems = new Text();;
    private int calories = 0; //Holds calculated calories value
    private int fat = 0; //Holds calculated fat value
    private int carbs = 0; //Holds calculated carbs value
    private int fiber = 0; //Holds calculated fiber value
    private int protein = 0;//Holds calculated protein value
    private String foods = ""; //Holds list of foods for meal summary
    private Text totalCaloriesVal = new Text(calories + " cal"); //Displays cal
    private Text totalProteinVal = new Text(protein + " g");//Displays protein
    private Text totalFiberVal = new Text(fiber + " g"); //Displays fiber
    private Text totalFatVal = new Text(fat + " g"); //Displays fat
    private Text totalCarbsVal = new Text(carbs + " g"); //Displays carbs



    private void updateMealSummary() {
        //gets the names of all the foods in the meal list
        System.out.print(mealList.size());
        for (FoodItem food : mealList) {
            foods += food.getName() + " (" + food.getID() + ")" + "\n";
        }
        foodItems.setText(foods);
        //runs through each items in the meal list and gets their nutrients
        for (FoodItem food : mealList) {
            HashMap<String, Double> nutrients = food.getNutrients();
            if(!nutrients.isEmpty()) {
                calories += nutrients.get("Calories");
                fat += nutrients.get("Fat");
                protein += nutrients.get("Protein");
                carbs += nutrients.get("Carbs");
                fiber += nutrients.get("Fiber");
            }

        }
        //Calorie information
        totalCaloriesVal.setText(calories + " cal");

        //Fat information
        totalFatVal.setText(fat + " g");

        //Carbohydrate information
        totalCarbsVal.setText(carbs + " g");

        //Fiber information
        totalFiberVal.setText(fiber + " g");

        //Protein information
        totalProteinVal.setText(protein + " g");
    }


    /**
     * Code for the navigation bar. Allows us to go back and forth between the different scenes
     * Functions as an escape route for the user. 
     * 
     * @param Stage primaryStage
     * @return TilePane returns the buttons
     */
    private TilePane navBar(Stage primaryStage) {
        //Home button allows the user to navigate to the home page
        Button homeButton= new Button("Home");
        homeButton.setMaxWidth(150);
        //sets the action of the navigation
        homeButton.setOnAction(e -> primaryStage.setScene(homeScene));

        //Add food button allows the user to navigate to the add food page
        Button addFoodButton= new Button("Add Food");
        addFoodButton.setMaxWidth(150);
        //sets the action of the navigation
        addFoodButton.setOnAction(e -> primaryStage.setScene(addFoodScene));

        //load food button allows the user to navigate to the load food page
        Button loadFoodButton= new Button("Load Food");
        loadFoodButton.setMaxWidth(150);
        //sets the action of the navigation
        loadFoodButton.setOnAction(e -> primaryStage.setScene(loadScene));

        //Meal List button allows the user to navigate to the meal list page
        Button mealListButton= new Button("Meal List");
        mealListButton.setMaxWidth(150);
        //sets the action of the navigation
        mealListButton.setOnAction(e -> {primaryStage.setScene(mealListScene);
        updateMealList();
        });

        //Meal summary button allows the user to navigate to the meal summary page
        Button mealSummaryButton= new Button("Meal Summary");
        mealSummaryButton.setMaxWidth(150);
        //sets the action of the navigation
        mealSummaryButton.setOnAction(e -> {
            primaryStage.setScene(mealSummaryScene);
        });

        //This is another way to create a horizontal box using a tile pane
        TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        //Sets the distance between the buttons
        tileButtons.setHgap(36.499);
        //Sets the distance between the buttons and the next box
        tileButtons.setVgap(8.0);
        tileButtons.getChildren().addAll(homeButton, addFoodButton, loadFoodButton, mealListButton, mealSummaryButton);
        return tileButtons;
    }
    /**
     * This is the scene where the user is able to load in food items
     * from a file.
     * 
     * @param Stage primaryStage
     * @param TilePane tilebuttons
     * @return Scene returns the load food page
     */
    private Scene LoadFoodScene(Stage primaryStage, TilePane tileButtons) {
        //Labels and instructions on how to use page
        Label loadFoodLabel = new Label("Load Food");
        loadFoodLabel.setFont(new Font("Serif", 40));
        Label instructionsForLoad = new Label("Type a file name containing information about food items. \nNeed to enter food in manually? \nClick the Enter Manually button below.");

        //Creates a button that lets the user enter in their own personal food
        Button enterManually = new Button("Enter Manually");
        enterManually.setMaxWidth(150);
        //Takes user to add food page
        enterManually.setOnAction(e -> primaryStage.setScene(addFoodScene));

        //Labels and creates the text field for the user to input a file
        Label fileLabel = new Label("Input File Name");
        TextField textField = new TextField();
        textField.setMaxSize(150, 100);

        //Creates a horizontal box to hold the two buttons that work with 
        //the text field
        HBox buttonsForLoad = new HBox(50);
        //Allows the user to delete the entry 
        Button clearButton = new Button ("Clear");
        //Actually does the action of clearing
        clearButton.setOnAction(e -> {
            textField.clear();
        });
        //Allows the user to import the file
        Button importButton = new Button("Import");

        //Does the action of importing
        //Uses the food data object food inorder to load the food items into
        //the program
        importButton.setOnAction(e -> {
            String fileName = String.valueOf(textField.getText());
            food.loadFoodItems(fileName);
            //            foodList.clear();
            for (int i = 0; i <food.getAllFoodItems().size(); i++) {
                foodList.add(food.getAllFoodItems().get(i));
            }


            for (int i = 0; i < foodList.size(); i++) {

                listView.getItems().add(foodList.get(i).getName());
            }

        });
        //enters all the horizontal box items into the scene
        buttonsForLoad.getChildren().addAll(clearButton, importButton);

        //Enters all the buttons and labels in to a vertical box
        VBox loadFoodPageLayout= new VBox(20);
        loadFoodPageLayout.getChildren().addAll(tileButtons, loadFoodLabel, instructionsForLoad, enterManually, fileLabel, textField, buttonsForLoad);

        //creates the scene based on all the style choices made above
        loadScene = new Scene(loadFoodPageLayout, 750, 700);
        return loadScene;
    }

    /**
     * This is the scene where the user is able to add in food items.
     * The text entries are labeled with the information needed.
     * Puts the new entry into the food list
     * 
     * @param Stage primaryStage
     * @param TilePane tilebuttons
     * @return Scene returns the load food page
     */
    private Scene addFoodScene(Stage primaryStage, TilePane tileButtons2) {
        // Alert for valid input
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Invalid Input");
        a.setHeaderText("Input not valid \nCalories, Fat, Carbs, Protein, and Fiber should a have numerical values");
        a.setResizable(true);

        //Creates different fonts and sizes for the page
        Font myFont = new Font("Serif", 50);
        Font myFont2 = new Font("Serif", 15);
        Font myFont3 = new Font("Serif", 20);

        //creates a V box that holds the nav bar and the first label
        VBox parent = new VBox();
        parent.setPrefSize(700, 700);

        //heading label
        Label label1 = new Label("Add Food");
        //Style choices
        label1.setMinHeight(70);
        label1.setMaxHeight(90);
        label1.setFont(myFont);

        //Instructions
        Label info = new Label("Add a food to the list from this page!");
        //Style choices
        info.setFont(myFont3);
        info.setMaxHeight(60);
        info.setMinHeight(50);

        //This creates the name text field information
        Label label2 = new Label("Name");
        //Style choices
        label2.setFont(myFont3);
        label2.setMinWidth(100);
        //creates the horizontal area between the label and the text field
        HBox urlArea = new HBox(label2);
        TextField txt = new TextField();
        //Style choices
        txt.setFont(myFont2);
        urlArea.getChildren().add(txt);
        urlArea.setMinHeight(50);

        Label id = new Label("ID");
        //Style choices
        id.setFont(myFont3);
        id.setMinWidth(100);
        //creates the horizontal area between the label and the text field
        HBox idB = new HBox(id);
        TextField Itxt = new TextField();
        //Style choices
        Itxt.setFont(myFont2);
        idB.getChildren().add(Itxt);
        idB.setMinHeight(50);

        Label calories = new Label("Calories");
        //Style choices
        calories.setFont(myFont3);
        calories.setMinWidth(100);
        //creates the horizontal area between the label and the text field
        HBox caloriesB = new HBox(calories);
        TextField Ctxt = new TextField();
        //Style choices
        Ctxt.setFont(myFont2);
        caloriesB.getChildren().add(Ctxt);
        caloriesB.setMinHeight(50);


        Label fat = new Label("Fat");
        //Style choices
        fat.setMinWidth(100);
        fat.setFont(myFont3);
        //creates the horizontal area between the label and the text field
        HBox fatb = new HBox(fat);
        TextField Ftxt = new TextField();
        //Style choices
        Ftxt.setFont(myFont2);
        fatb.getChildren().add(Ftxt);
        fatb.setMinHeight(50);


        Label carbs = new Label("Carbs");
        //Style choices
        carbs.setMinWidth(100);
        carbs.setFont(myFont3);
        //creates the horizontal area between the label and the text field
        HBox carbsB = new HBox(carbs);
        TextField btxt = new TextField();
        //Style choices
        btxt.setFont(myFont2);
        carbsB.getChildren().add(btxt);
        carbsB.setMinHeight(50);


        Label fiber = new Label("Fiber");
        //Style choices
        fiber.setMinWidth(100);
        fiber.setFont(myFont3);
        //creates the horizontal area between the label and the text field
        HBox fiberB = new HBox(fiber);
        TextField ftxt = new TextField();
        //Style choices
        ftxt.setFont(myFont2);
        fiberB.getChildren().add(ftxt);
        fiberB.setMinHeight(50);



        Label protein = new Label("Protein");
        //Style choices
        protein.setMinWidth(100);
        protein.setFont(myFont3);
        //creates the horizontal area between the label and the text field
        HBox proteinB = new HBox(protein);
        TextField ptxt = new TextField();
        //Style choices
        ptxt.setFont(myFont2);
        proteinB.getChildren().add(ptxt);
        proteinB.setMinHeight(50);


        HBox buttons = new HBox(122);
        Button add = new Button("Add to Food List");
        Button clear = new Button ("Clear");
        //Clears all the text fields
        clear.setOnAction(e -> {
            ptxt.clear();
            ftxt.clear();
            btxt.clear();
            Ctxt.clear();
            Ftxt.clear();
            Itxt.clear(); 
            txt.clear();
        });
        //creates a new food item and adds it to the food list
        add.setOnAction(e -> {
            //gets the values from the text field
            String name = String.valueOf(txt.getText());
            String idFood = String.valueOf(Itxt.getText());
            FoodItem itemFood = new FoodItem(idFood, name);
            Double calorie = 0.0;
            Double fatFood = 0.0;
            Double carbsFood = 0.0;
            Double fiberFood =0.0;
            Double proteinFood= 0.0;
            try { 
                calorie = Double.parseDouble(Ctxt.getText());
                fatFood =  Double.valueOf(Ftxt.getText());
                carbsFood =  Double.valueOf(btxt.getText());
                fiberFood =  Double.valueOf(ftxt.getText());
                proteinFood =  Double.valueOf(ptxt.getText());
            }catch(NumberFormatException p) {
                System.out.println("Cannot be a letter");
                a.showAndWait();
                return;
            }
            if(calorie<0 | fatFood<0 | carbsFood<0|fiberFood<0|proteinFood<0) {
                System.out.println("Must be positive");
                return;
            }


            //added the values to the food item
            itemFood.addNutrient("Calories", calorie);
            itemFood.addNutrient("Fat", fatFood);
            itemFood.addNutrient("Protein", proteinFood);
            itemFood.addNutrient("Fiber", fiberFood);
            itemFood.addNutrient("Carbs", carbsFood);
            //adds it to food data
            listView.getItems().add(name);
            food.addFoodItem(itemFood);
            foodList = food.getAllFoodItems();
            foodList = sort();
            listView.getItems().clear();
            for(int i = 0; i < foodList.size(); i++) {
                listView.getItems().add(foodList.get(i).getName());
            }
        });

        //creates the add and clear buttons
        buttons.getChildren().addAll(add,clear);

        //creates the nav bar, instructions, and label
        parent.getChildren().addAll(tileButtons2, label1, info, urlArea,idB,caloriesB,fatb,carbsB,fiberB,proteinB,buttons);

        //creates the new scene
        addFoodScene = new Scene(parent,750,700);
        addFoodScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        return addFoodScene;
    }
    /**
     * This scene is where our meal list is created. The user can put
     * foods into the list to then be shown in the meal scene
     * 
     * @param Stage primaryStage
     * @para TilePane returns the buttons
     * @return Scene returns the mealScene page

     */
    private Scene mealScene(Stage primaryStage, TilePane tileButtons) {
        //Style choices        
        Font myFont = new Font("Serif", 30);

        //Creates the heading for the page
        primaryStage.setTitle("Meal List");
        //creates the initial vertical box
        VBox parent = new VBox();
        parent.setPrefSize(700, 700);
        parent.getChildren().add(tileButtons);

        //Creates a horizontal box
        HBox parent1 = new HBox(100);

        //Creates the food list label
        Label list = new Label("Meal List");
        list.setFont(myFont);

        //Creates the actual list of foods 
        ListView<String> test = new ListView<String>();
        items =FXCollections.observableArrayList ();
        test.setItems(items);



        VBox foodList = new VBox();
        foodList.getChildren().addAll(list, test);
        parent1.getChildren().add(foodList);

        VBox buttons = new VBox(30);
        //sets space above the buttons 
        Label label = new Label();
        label.setMinHeight(50);

        //creates a save to file button

        //Takes you to the meal summary page
        Button view = new Button("Analyze Meal");
        Button clear = new Button("Clear");
        clear.setPrefSize(350, 100);
        //Does the action of taking you to the meal summary page
        view.setOnAction(e -> {

            primaryStage.setScene(mealSummaryScene);
            updateMealSummary();//updates values in scene
        });
        view.setPrefSize(350, 100);
        clear.setOnAction(e -> clearMealList());
        buttons.getChildren().addAll(clear,view);
        parent1.getChildren().add(buttons);

        parent.getChildren().add(parent1);
        //creates the meal list scene
        mealListScene = new Scene(parent,750,700);

        mealListScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        return mealListScene;
    }
    /**
     * This scene has all the information from the food selected
     * It calculates the total nutrients from the foods picked
     * by the user and displays them in a format that is easily 
     * understood
     * 
     * @param Stage primaryStage
     * @param TilePane returns the buttons
     * @return Scene returns the meal summary page

     */
    private Scene mealSummaryScene(Stage primaryStage, TilePane tileButtons) {
        //Style choice
        Font myFont = new Font("Serif", 30);


        VBox parent = new VBox();
        parent.setPrefSize(700, 700);

        //Sets the heading
        Label mealSummaryTitle = new Label("Meal Summary");
        //Style choices
        mealSummaryTitle.setMinHeight(70);
        mealSummaryTitle.setMaxHeight(90);
        mealSummaryTitle.setFont(myFont);

        //Lets you navigate back to the meal list
        Button modifyFoods = new Button("Modify Foods in this Meal");
        //Style Choices
        modifyFoods.setMaxWidth(150);
        modifyFoods.setWrapText(true);
        //does the action of navigation
        modifyFoods.setOnAction(e -> primaryStage.setScene(mealListScene));

        //Allows you to navigate back to the home page
        Button modifyQuery = new Button("Add More Foods");
        modifyQuery.setMaxWidth(150);
        //Does the action of navigation
        modifyQuery.setOnAction(e -> primaryStage.setScene(homeScene));


        //Subheading
        Text foodNames = new Text("Foods:");
        foodNames.setId("bold");
        //Displays foods in meal list

        //Calorie information
        Text totalCalories = new Text("\nTotal Calories");
        totalCalories.setId("bold");

        //Fat information
        Text totalFat = new Text("\nTotal Fat");
        totalFat.setId("bold");

        //Carbohydrate information
        Text totalCarbs = new Text("\nTotal Carbohydrates");
        totalCarbs.setId("bold");

        //Fiber information
        Text totalFiber = new Text("\nTotal Fiber");
        totalFiber.setId("bold");

        //Protein information
        Text totalProtein = new Text("\nTotal Protein");
        totalProtein.setId("bold");


        HBox content = new HBox();
        VBox left = new VBox();
        VBox right = new VBox();
        left.setTranslateX(100);
        right.setTranslateX(150);
        left.getChildren().addAll(modifyFoods, modifyQuery);
        right.getChildren().addAll(foodNames, foodItems, totalCalories, totalCaloriesVal, totalFat, totalFatVal, totalCarbs, totalCarbsVal, totalFiber, totalFiberVal, totalProtein, totalProteinVal);
        content.getChildren().addAll(left, right);

        parent.getChildren().addAll(tileButtons, mealSummaryTitle, content);
        //Creates the meal scene
        mealSummaryScene = new Scene(parent, 750, 700);

        mealSummaryScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        return mealSummaryScene;
    }
    /**
     * This scene is the home page. It has the meal query and 
     * a list of all the foods in our data base. It also has an analyze
     * meal button if the user doesn't want to see the foods in their list
     * 
     * @param Stage primaryStage
     * @param TilePane returns the buttons
     * @return Scene returns the meal summary page

     */
    private Scene homeScene(Stage primaryStage, TilePane tileButtons) {
        //Style choises
        Font myFont1 = new Font("Serif", 30);
        Font myFont2 = new Font("Serif", 20);
        Font myFont3 = new Font("Serif", 15);
        primaryStage.setTitle("Food Query & Meal Analysis");
        VBox root = new VBox();
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(36.499);
        tileButtons.setVgap(8.0);



        VBox content = new VBox(25);
        content.setPrefSize(700, 650);
        Label mealQuery = new Label("Home Page");
        mealQuery.setFont(myFont1);

        //BEGINNING foods in meal
        //parent VBox
        VBox foodsInMeal = new VBox(10);
        //label of vbox
        Label foods = new Label("List of Foods");
        foods.setTranslateX(310);
        foods.setFont(myFont2);

        //hbox below label, contains check box, scroll bar, remove button
        HBox foodsHBox = new HBox();
        foodsHBox.setAlignment(Pos.CENTER);

        //list of foods in food
        listView = new ListView<String>();
        for (int i = 0; i < filteredFoodList.size(); i++) {
            String[] splitName = filteredFoodList.get(i).getName().split("_");
            String shortened = splitName[0] + splitName[1].substring(0, splitName[1].length()/2);
            listView.getItems().add(shortened);
        }
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Scrollpane to contain contents of the foodList
        ScrollPane foodsSP = new ScrollPane();
        foodsSP.setContent(listView);
        foodsSP.setPrefSize(250, 150);
        foodsSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        foodsSP.setFitToHeight(true);
        foodsSP.setHmax(100);

        //remove button
        //Button removeFoodButton = new Button("Remove");
        HBox buttons = new HBox(520);
        addToMeal = new Button("Add To Meal");
        saveButton = new Button("Save Food List");
        saveButton.setOnAction(e-> food.saveFoodItems("foodItems.csv"));
        buttons.getChildren().addAll(addToMeal, saveButton);

        Label instructions = new Label("Windows user: control click for multiple\nApple user: command click for multiple");

        //Adding checkboxes, scrollbars, remove button to bottom hbox
        foodsHBox.getChildren().addAll(listView, foodsSP);
        //adds label and bottom hbox to parent vbox
        foodsInMeal.getChildren().addAll(foods, foodsHBox, buttons, instructions);

        //END foods in meal
        addToMeal.setOnAction(e -> addToMealClicked(listView));

        //BEGINNING filters to apply
        //parent vbox
        VBox applyFilter = new VBox(10);
        Label filters = new Label("Apply a Filter");
        filters.setTranslateX(250);
        filters.setFont(myFont2);
        //hbox that has filter dropdown, comparison dropdown, and value input
        HBox filtersHBox = new HBox(100);
        filtersHBox.setAlignment(Pos.CENTER);
        //filters
        ChoiceBox filterDropDown = new ChoiceBox();
        filterDropDown.setItems(FXCollections.observableArrayList(
                        "Calories", "Fat", "Carbs", "Fiber", "Protein"));
        //comparisons
        ChoiceBox comparisonDropDown = new ChoiceBox();
        comparisonDropDown.setItems(FXCollections.observableArrayList(
                        ">=", "<=", "="));
        //value input textfield for
        TextField inputValue = new TextField("0");
        //button to apply filter
        List<String> filterList = new ArrayList<String>();
        Button apply = new Button("Apply Filter");
        apply.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(ActionEvent e) {
                
                //Account for errors thrown for calling methods on potential null values, helps ensure no null values are being
                //passed in
                try {
                    if (!filterDropDown.getValue().equals(null) &&
                                    !comparisonDropDown.getValue().equals(null) &&
                                    Integer.parseInt(inputValue.getText().toString()) >= 0) {
                        Object nutrient = filterDropDown.getValue();
                        Object comparison = comparisonDropDown.getValue();
                        int IV = Integer.parseInt(inputValue.getText());
                        //checks for duplicate filter
                        boolean duplicate = false;
                        
                        //checks each filter to make sure that there are no duplicates-- only need to check for same
                        //nutrient
                        for (int i = 0; i < filterList.size(); i++) {
                            if (filterList.size() > 0) {
                                String[] filtersArray = filterList.get(i).split(",");
                                if (filtersArray[0] == nutrient.toString()) {
                                    duplicate = true;
                                }
                            }
                        }
                        
                        //if there is no duplicate, add each filter to a list which is then passed into list that holds each set
                        //of filters. Values can just be passed straight into the lists but keeping track of filters in a list
                        //helps detect duplicates.
                        if (!duplicate) {
                            String filter = "";
                            filter = filter + nutrient + "," + comparison + "," + IV;
                            filterList.add(filter);
                            nutrientsList.getItems().add(nutrient);
                            comparisonsList.getItems().add(comparison);
                            inputList.getItems().add(IV);
                            filterCount++;
                        }

                        //Meal analysis
                        filteredListView = new ListView<String>();
                        //Calories
                        if (nutrient.toString() == "Calories") {
                            //>=
                            if (comparison.toString() == ">=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Calories") < IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //<=
                            if (comparison.toString() == "<=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Calories") > IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //=
                            if (comparison.toString() == "=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Calories") != IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                        }
                        //Fat
                        if (nutrient.toString() == "Fat") {
                            //>=
                            if (comparison.toString() == ">=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fat") < IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //<=
                            if (comparison.toString() == "<=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fat") > IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //=
                            if (comparison.toString() == "=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fat") != IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                        }

                        //Carbohydrate
                        if (nutrient.toString() == "Carbs") {
                            //>=
                            if (comparison.toString() == ">=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Carbs") < IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //<=
                            if (comparison.toString() == "<=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Carbs") > IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //=
                            if (comparison.toString() == "=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Carbs") != IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                        }

                        //Fiber
                        if (nutrient.toString() == "Fiber") {
                            //>=
                            if (comparison.toString() == ">=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fiber") < IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //<=
                            if (comparison.toString() == "<=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fiber") > IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //=
                            if (comparison.toString() == "=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Fiber") != IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                        }
                        //Protein
                        if (nutrient.toString() == "Protein") {
                            //>=
                            if (comparison.toString() == ">=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Protein") < IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //<=
                            if (comparison.toString() == "<=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Protein") > IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                            //=
                            if (comparison.toString() == "=") {
                                for (int i = filteredFoodList.size() - 1; i >= 0; i--) {
                                    if (filteredFoodList.get(i).getNutrientValue("Protein") != IV) {
                                        filteredFoodList.remove(i);
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < filteredFoodList.size(); i++) {
                            String[] splitName = filteredFoodList.get(i).getName().split("_");
                            String shortened = splitName[0] + splitName[1].substring(0, splitName[1].length()/2);
                            filteredListView.getItems().add(shortened);
                        }
                        filteredListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                        listView = filteredListView;

                        foodsSP.setContent(listView);
                    }
                } catch(Exception nullValue) {
                }
            }
        });


        //Add filters dropdown, comparison dropdown, input value
        filtersHBox.getChildren().addAll(filterDropDown, comparisonDropDown, inputValue, apply);
        //add label and bottom hbox to parent vbox
        applyFilter.getChildren().addAll(filters, filtersHBox);
        //END filters to apply

        
        //BEGINNING Filters applied menu
        //Parent vbox
        VBox appliedFilters = new VBox(10);
        Label applied = new Label("Applied Filters");
        applied.setTranslateX(250);
        applied.setFont(myFont2);
        HBox appliedFiltersHBox = new HBox(5);
        HBox currentFilters = new HBox(); //hbox that has display of filters applied, comparisons applied, and input value
        currentFilters.setPrefWidth(675);
        currentFilters.setPrefHeight(117);
        currentFilters.getChildren().addAll(nutrientsList, comparisonsList, inputList);
        //remove filter button
        VBox removeFilterVBox = new VBox();
        removeFilterVBox.setPrefWidth(175);
        Button removeFilter = new Button("Remove Filters");
        //Deals with removing filters
        removeFilter.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override public void handle(ActionEvent e) {
                if (filterCount > 0) {
                    for (int i = filterCount - 1; i >= 0; i--) {
                        nutrientsList.getItems().remove(i);
                        comparisonsList.getItems().remove(i);
                        inputList.getItems().remove(i);
                    }
                    filterCount = 0;
                }

                filteredFoodList = new ArrayList<FoodItem>();

                for (int i = 0; i <food.getAllFoodItems().size(); i++) {
                    filteredFoodList.add(food.getAllFoodItems().get(i));
                }

                filteredListView = new ListView<String>();

                for (int i = 0; i < filteredFoodList.size(); i++) {
                    String[] splitName = filteredFoodList.get(i).getName().split("_");
                    String shortened = splitName[0] + splitName[1].substring(0, splitName[1].length()/2);
                    filteredListView.getItems().add(shortened);
                }
                filteredListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                listView = filteredListView;

                foodsSP.setContent(listView);

            }
        });
        removeFilterVBox.getChildren().add(removeFilter);
        appliedFiltersHBox.getChildren().addAll(currentFilters, removeFilterVBox);
        appliedFilters.getChildren().addAll(applied, appliedFiltersHBox);



        //Add all parent vboxes
        content.getChildren().addAll(mealQuery, foodsInMeal, applyFilter, appliedFilters);


        //Creating scene with VBox root as node
        root.getChildren().addAll(tileButtons, content);
        homeScene = new Scene(root, 750, 700);


        return homeScene;


    }
    /**
     * This method starts the program. It runs all the scenes in order
     * for the user to interact with them.
     * 
     * @param Stage primaryStage
     */
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("My First JavaFX GUI");
        try {
            //Home Page Scene
            TilePane tileButtons = navBar(primaryStage);
            homeScene = homeScene(primaryStage,tileButtons);
            //Load Food Scene
            TilePane tileButtons2 = navBar(primaryStage);
            loadScene = LoadFoodScene(primaryStage, tileButtons2);
            //Add Food Scene
            TilePane tileButtons3 = navBar(primaryStage);
            addFoodScene = addFoodScene(primaryStage, tileButtons3);
            //Meal Query
            TilePane tileButtons4 = navBar(primaryStage);
            mealListScene = mealScene(primaryStage, tileButtons4);
            //Meal Summary
            TilePane tileButtons5 = navBar(primaryStage);
            mealSummaryScene = mealSummaryScene(primaryStage, tileButtons5);

            primaryStage.setScene(homeScene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }




    }
    /**
     * Sorts the items
     * 
     */
    private void sorted() {
        Collections.sort(items);

    }
    /**
     * Puts sorted picked items into the meal list
     * 
     */
    private void sortedMealList() {
        //runs through the names in items
        for(String name : items) {
            //runs through food items
            for(FoodItem item : foodList) {
                //seeing if the name of the food item matches the nae of the item
                if(item.getName().equals(name)) {
                    //adds to the meallist
                    mealList.add(item);
                    break;
                }

            }
        }
    }
    /**
     *This method updates the items displays in the meal list
     * 
     */
    private void updateMealList() {
        if(!(choosenFoods.isEmpty())) {
            for(int i = 0; i<choosenFoods.size(); i++) {
                items.add(choosenFoods.get(i));
            }
        }else {
            return;
        }
        sorted();
        sortedMealList();
        choosenFoods.clear();
    }
    /**
     * This method clears all the items in the meal list and clears the analyzed menu
     */
    private void clearMealList() {
        for(int i = 0; i<items.size();i++) {
            items.clear();
        }
        mealList = new ArrayList<FoodItem>();
        foods = "";
        calories = 0;
        fiber = 0;
        fat = 0;
        carbs = 0;
        protein = 0;

    } 
    /**
     * When add to meal button is clicked this method adds
     * the selected foods to the meal
     * @param listView
     */
    private void addToMealClicked(ListView<String> listView) {
        choosenFoods = new ArrayList<String>();
        ObservableList<String> foods;
        foods = listView.getSelectionModel().getSelectedItems();

        for(String f: foods) {


            choosenFoods.add(f);    


        }
    }

    private List<FoodItem> sort(){
        List<String> sorted = new ArrayList<String>();
        List<FoodItem> sortedItems = new ArrayList<FoodItem>();
        for(FoodItem item : foodList) {
            sorted.add(item.getName().toLowerCase());
        }
        Collections.sort(sorted);
        for(String name : sorted) {
            for(FoodItem item : foodList) {
                if(item.getName().toLowerCase().equals(name)) {
                    sortedItems.add(item);
                }
            }
        }
        return sortedItems;
    } 

    public static void main(String[] args) {
        launch(args);
    }

}
