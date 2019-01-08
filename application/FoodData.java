package application;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.AbstractDocument.BranchElement;
/**
 * Filename:   FoodData.java
 * Project:    p5
 * Course:     cs400 
 * Authors: Vinnie, David, Charles, Monica   
 * Due Date:   10/12
 * 
 *
 * Additional credits:
 *
 * Bugs or other notes: filterNutrients does not work in foodData
 *
 */

/**
 * Filename:   FoodData.java
 * Project:    p5
 * Course:     cs400 Fall 2018
 * Authors:   Vinnie Angellotti, David Portugal, Charles Houghtaling, Monica Schmidt 
 * Due Date:  10/12 
 * 
 * This class represents the back-end for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {

    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    //Different BPTrees for different nutrients
    private BPTree<Double, FoodItem> tempCal;
    private BPTree<Double, FoodItem> tempFat;
    private BPTree<Double, FoodItem> tempCarb;
    private BPTree<Double, FoodItem> tempFib;
    private BPTree<Double, FoodItem> tempPro;
    //Holds the nutrients for different foodItems, is accessable to whole class
    private HashMap<String, Double> nutrients;

    /**
     * Public constructor
     */
    public FoodData() {
        tempCal = new BPTree<Double,FoodItem>(3); 
        tempFat = new BPTree<Double,FoodItem>(3);
        tempCarb = new BPTree<Double,FoodItem>(3);
        tempFib = new BPTree<Double,FoodItem>(3);
        tempPro = new BPTree<Double,FoodItem>(3);
        foodItemList = new ArrayList<FoodItem>();
        indexes = new HashMap<String,BPTree<Double,FoodItem>>();
    }
    /**
     * Sorts the items in the food list by their name 
     * 
     * @param List<FoodItem>
     */
    private List<FoodItem> sort(){
        //List of strings to hold the names
        List<String> sorted = new ArrayList<String>();
        //List of foodItems to hold the sorted items
        List<FoodItem> sortedItems = new ArrayList<FoodItem>();
        //Runs through each item in food list and grabs its name
        for(FoodItem item : foodItemList) {
            sorted.add(item.getName().toLowerCase());
        }
        //sorts the names
        Collections.sort(sorted);
        //Runs through the names and the foodItemList 
        for(String name : sorted) {
            for(FoodItem item : foodItemList) {
                //puts the items into foodItemList in sorted order
                if(item.getName().toLowerCase().equals(name)) {
                    sortedItems.add(item);
                }
            }
        }
        return sortedItems;
    }


    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            String [] details;


            //Checks to see if the line is empty
            while(!(line = br.readLine()).equals(",,,,,,,,,,,")){
                //Creates an array of all the items in the line read in
                details = line.split(",");
                //Makes sure the item is in the correct format
                if(details.length == 12) {
                    String id = details[0]; //id value 
                    String name = details[1]; //name value
                    Double calories = Double.parseDouble(details[3]); //calorie value
                    Double fat = Double.parseDouble(details[5]);//fat value
                    Double carbohydrate = Double.parseDouble(details[7]);//carb value
                    Double fiber = Double.parseDouble(details[9]);//fiber value
                    Double protein = Double.parseDouble(details[11]);//protein value
                    //Creates a temporary food item to add to the list
                    FoodItem tempItem = new FoodItem(id, name);
                    //Sorted list implementation
                    tempItem.addNutrient("Calories", calories);
                    tempItem.addNutrient("Fat", fat);
                    tempItem.addNutrient("Carbs", carbohydrate);
                    tempItem.addNutrient("Fiber", fiber);
                    tempItem.addNutrient("Protein", protein);
                    //b tree implementation
                    tempFat.insert(fat, tempItem);
                    tempCarb.insert(carbohydrate, tempItem);
                    tempFib.insert(fiber, tempItem);
                    tempPro.insert(protein, tempItem);
                    tempCal.insert(calories, tempItem);
                    tempFat.insert(fat, tempItem);
                    tempCarb.insert(carbohydrate, tempItem);
                    tempFib.insert(fiber, tempItem);
                    tempPro.insert(protein, tempItem);
                    foodItemList.add(tempItem);
                    //resets tempitem
                    tempItem = null;
                }

            }
            //closes the reader
            br.close();
            //puts the values into indexes
            indexes.put("Carlories", tempCal);
            indexes.put("Fat", tempFat);
            indexes.put("Carbs", tempCarb);
            indexes.put("Fiber", tempFib);
            indexes.put("Protein", tempPro);
        }catch(Exception e) {
            e.printStackTrace();

        }
        //sorts foodItemList
        foodItemList = sort();

    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        //Creates an array for all the items filtered by name
        List<FoodItem> filteredByName = new ArrayList<FoodItem>();
        //searches the foodItemList
        for (FoodItem item : foodItemList) {
            //sees if the name contains the substring
            if(item.getName().toLowerCase().contains(substring.toLowerCase())) {
                //adds it to the list
                filteredByName.add(item); 
            }
        }
        return filteredByName;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
        return null;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        foodItemList.add(foodItem);//adds food item to list
        //inserts the nutritional values into the nutrient hash map of the food item
        tempCal.insert(foodItem.getNutrientValue("Calories"), foodItem);
        tempFat.insert(foodItem.getNutrientValue("Fat"), foodItem);
        tempCarb.insert(foodItem.getNutrientValue("Carbs"), foodItem);
        tempFib.insert(foodItem.getNutrientValue("Fiber"), foodItem);
        tempPro.insert(foodItem.getNutrientValue("Protein"), foodItem);

        indexes.remove("Calories");
        indexes.remove("Fat");
        indexes.remove("Carbs");
        indexes.remove("Fiber");
        indexes.remove("Protein");
        //inserts the nutritional values into indexes
        indexes.put("Carlories", tempCal);
        indexes.put("Fat", tempFat);
        indexes.put("Carbs", tempCarb);
        indexes.put("Fiber", tempFib);
        indexes.put("Protein", tempPro);
        //sorts the foodItemList
        foodItemList = sort();
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    }
    /**
     * Gets the nutrient hash map from the food item
     * @param FoodItem
     */
    private void getNutrients(FoodItem food) {
        nutrients = food.getNutrients();
        
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public void saveFoodItems(String filename) {

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            //Runs through the items in foodItemList
            for(int i = 0; i< foodItemList.size();i++) {
                //Gets the nutrients for the item
                getNutrients(foodItemList.get(i));
                //Writes a new formated line
                writer.write(foodItemList.get(i).getID() + "," + foodItemList.get(i).getName() + ",calories," + nutrients.get("Calories") + ",fat," + nutrients.get("Fat") + ",carbohydrate," + nutrients.get("Carbs") + ",fiber," + nutrients.get("Fiber") + ",protein," + nutrients.get("Protein"));
            }




        } catch (IOException e) {

            e.printStackTrace();
        }




    }
   

}