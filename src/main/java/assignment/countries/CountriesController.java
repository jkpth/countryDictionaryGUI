package assignment.countries;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ouda
 */
public class CountriesController implements Initializable {

    @FXML
    private MenuBar mainMenu;
    @FXML
    private ImageView image;
    @FXML
    private BorderPane CountryPortal;
    @FXML
    private Label title;
    @FXML
    private Label about;
    @FXML
    private Button play;
    @FXML
    private Button puase;
    @FXML
    private ComboBox size;
    @FXML
    private TextField name;
    Media media;
    MediaPlayer player;
    OrderedDictionary database = null;
    CountryRecord country = null;
    int countrySize = 1;

    @FXML
    public void exit() {
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        stage.close();
    }

    public void find() {
        DataKey key = new DataKey(this.name.getText(), countrySize);
        try {
            country = database.find(key);
            showCountry();
        } catch (DictionaryException ex) {
            displayAlert(ex.getMessage());
        }
    }

    public void delete() {
        CountryRecord previousCountry = null;
        try {
            previousCountry = database.predecessor(country.getDataKey());
        } catch (DictionaryException ex) {

        }
        CountryRecord nextCountry = null;
        try {
            nextCountry = database.successor(country.getDataKey());
        } catch (DictionaryException ex) {

        }
        DataKey key = country.getDataKey();
        try {
            database.remove(key);
        } catch (DictionaryException ex) {
            System.out.println("Error in delete "+ ex);
        }
        if (database.isEmpty()) {
            this.CountryPortal.setVisible(false);
            displayAlert("No more Countries in the database to show");
        } else {
            if (previousCountry != null) {
                country = previousCountry;
                showCountry();
            } else if (nextCountry != null) {
                country = nextCountry;
                showCountry();
            }
        }
    }

    private void showCountry() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
        String img = country.getImage();
        Image countryImage = new Image("file:src/main/resources/assignment/countries/flags/" + img);
        image.setImage(countryImage);
        title.setText(country.getDataKey().getCountryName());
        about.setText(country.getAbout());
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/assignment/countries/flags/globe.png"));
            stage.setTitle("Dictionary Exception");
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    public void getSize() {
        switch (this.size.getValue().toString()) {
            case "Africa":
                this.countrySize = 1;
                break;
            case "Asia":
                this.countrySize = 2;
                break;
            case "Europe":
                this.countrySize = 3;
                break;
                case "North America":
                this.countrySize = 4;
                break;
            case "South America":
                this.countrySize = 5;
                break;
            case "Oceania":
                this.countrySize = 6;
                break;
            default:
                break;
        }
    }

    public void first() {
        try{
            country = database.smallest();
            if(country != null){
                showCountry();
            } else {
                System.out.println("The dictionary is empty.");
            }
        } catch (DictionaryException e){
            e.printStackTrace();
        }
    }

    public void last() {
        try{
            country = database.largest();
            if(country != null){
                showCountry();
            } else {
                System.out.println("The dictionary is empty.");
            }
        } catch (DictionaryException e){
            e.printStackTrace();
        }
    }

    public void next() {
        if(country==null){
            System.out.println("No current Country to find next for.");
            return;
        }
        try{
            CountryRecord nextCountry = database.successor(country.getDataKey());
            if(nextCountry != null){
                country = nextCountry; //Update current country to next country
                showCountry(); //Show the country
            }else{
                System.out.println("This is the last Country in the dictionary");
            }
        }catch(DictionaryException e){
            e.printStackTrace(); //Handle the exception
        }
    }

    public void previous() {
        if(country == null){
            System.out.println("No current Country to find previous for.");
            return;
        }
        try{
            CountryRecord previousCountry = database.predecessor(country.getDataKey());
            if(previousCountry != null){
                country = previousCountry; //Update current country to previous country
                showCountry(); //Show the country
            }else{
                System.out.println("This is the first Country in the dictionary.");
            }
        }catch(DictionaryException e){
            e.printStackTrace(); //Handle the exception
        }
        // Write this method
    }

    public void play() {
        String filename = "src/main/resources/assignment/countries/anthems/" + country.getSound();
        media = new Media(new File(filename).toURI().toString());
        player = new MediaPlayer(media);
        play.setDisable(true);
        puase.setDisable(false);
        player.play();
    }

    public void puase() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
    }

    public void loadDictionary() {
        Scanner input;
        int line = 0;
        try {
            String countryName = "";
            String description;
            int size = 0;
            input = new Scanner(new File("CountriesDatabase.txt"));
            while (input.hasNext()) // read until  end of file
            {
                String data = input.nextLine();
                switch (line % 3) {
                    case 0:
                        size = Integer.parseInt(data);
                        break;
                    case 1:
                        countryName = data;
                        break;
                    default:
                        description = data;
                        System.out.println("Inserting CountryRecord with DataKey: Name = " + countryName + ", Size = " + size);
                        database.insert(new CountryRecord(new DataKey(countryName, size), description, countryName + ".mp3", countryName + ".jpg"));
                        break;
                }
                line++;
            }
        } catch (IOException e) {
            System.out.println("There was an error in reading or opening the file: CountriesDatabase.txt");
            System.out.println(e.getMessage());
        } catch (DictionaryException ex) {
            Logger.getLogger(CountriesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.CountryPortal.setVisible(true);
        this.first();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database = new OrderedDictionary();
        size.setItems(FXCollections.observableArrayList(
            "Africa", "Asia", "Europe", "North America", "South America",
             "Oceania"));
        size.setValue("Africa");
    }

}
