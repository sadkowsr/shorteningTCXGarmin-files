
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainWindowController {

        private int count = 0 ;
        @FXML
        private Button run;

        @FXML
        private Button inputFileButton;

        @FXML
        private Button outputDirButton;

        @FXML
        private TextField outputDirField;

    @FXML
    private TextField inputFilesField;

    @FXML
    private TextField maxSizeField;




    public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    @FXML
    protected void handleInputFileButtonAction(ActionEvent event){
        inputFileButton.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose tcx files:");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "TCX files (*.tcx)", "*.tcx");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> list = fileChooser.showOpenMultipleDialog(inputFileButton.getScene().getWindow());
        if (list != null) {
        StringBuilder sb= new StringBuilder();
            for (File file : list) {
                sb.append(file.toString());
                sb.append(";");

            }
            inputFilesField.setText(sb.substring(0,sb.length()-1));
        }
        inputFileButton.setDisable(false);
    }

    @FXML
    protected void handleOutputDirButtonAction(ActionEvent event){
        outputDirButton.setDisable(true);
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Choose output directory");

        //Show open file dialog
        File file = directoryChooser.showDialog(null);

        if(file!=null && file.isDirectory()){

            outputDirField.setText(file.getPath());

        }
        outputDirButton.setDisable(false);
    }
        @FXML
        protected void handleSubmitButtonAction(ActionEvent event) {
            Window owner = run.getScene().getWindow();
            //List<String> filenames = new ArrayList<>();
            String fileNames = inputFilesField.getText();
            if(fileNames.length()==0)
                return;
            String[] fileNamesArray=fileNames.split(";");
            String outputDirectory =outputDirField.getText();
            if(outputDirectory.isEmpty())
                return;
            Integer maxSize=null;
            try{
                maxSize=Integer.parseInt(maxSizeField.getText());}
            catch(NumberFormatException nfe){
                System.err.println(nfe.toString());
                return;
            }

            for(String fileName:fileNamesArray)
                Model.analyse(fileName,outputDirectory,maxSize);

            showAlert(Alert.AlertType.INFORMATION, owner, "Transformation Successful!",
                    Arrays.toString(fileNamesArray));

        }


    }

