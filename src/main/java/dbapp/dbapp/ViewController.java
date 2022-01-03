package dbapp.dbapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;
import java.util.Objects;

public class ViewController {

    private static DBTalker talker;

    @FXML
    private TextField costFieldFrom;

    @FXML
    private TextField costFieldTo;

    @FXML
    private TextArea customArea;

    @FXML
    private Button customSearchBtn;

    @FXML
    private Button duplicatesBtn;

    @FXML
    private TextField locationField;

    @FXML
    private TextField mediaTypeField;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextField publisherField;

    @FXML
    private Button searchBtn;

    @FXML
    private Label statsAudioNumLabel;

    @FXML
    private Label statsAvgCostLabel;

    @FXML
    private Label statsDisksNum;

    @FXML
    private Label statsDocsNumLabel;

    @FXML
    private Label statsFilmNumLabel;

    @FXML
    private TextArea statsPlacesArea;

    @FXML
    private Label statsPopPubLabel;

    @FXML
    private Label statsPopTypeLabel;

    @FXML
    private Label statsSoftNumLabel;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField titleField;

    public TabPane getTabPane() {
        return tabPane;
    }

    public static void setTalker(DBTalker talker) {
        ViewController.talker = talker;
    }

    @FXML
    public void search(ActionEvent actionEvent) {
        String disks = null;

        String title = titleField.getText().trim();
        String place = locationField.getText().trim();
        String publisher = publisherField.getText().trim();
        String mediaType = mediaTypeField.getText().trim();

        Double costFrom;
        Double costTo;

        if (costFieldFrom.getText().trim().isEmpty()) {
            costFrom = null;
        } else {
            costFrom = Double.parseDouble(costFieldFrom.getText().trim());
        };

        if (costFieldTo.getText().trim().isEmpty()) {
            costTo = null;
        } else {
            costTo = Double.parseDouble(costFieldTo.getText().trim());
        }

        disks = talker.getDisksByParamsSet(title, place, publisher, mediaType, costFrom, costTo);

        if (disks == null || disks.isEmpty()) {
            outputArea.setText("Нет таких данных");
        } else {
            outputArea.setText(disks);
        }
    }

    @FXML
    public void customSearch(ActionEvent event) {
        if (customArea.getText().trim().isEmpty()) outputArea.setText("Не параметров");

        String disks = talker.getDisksByCustomParams(customArea.getText().trim());
        if (disks == null || disks.isEmpty()) {
            outputArea.setText("Нет таких дисков");
        } else {
            outputArea.setText(disks);
        }
    }

    @FXML
    public void getDuplicates(ActionEvent actionEvent) {
        String duplicates = talker.getDuplicates();
        if (duplicates.isEmpty()) {
            outputArea.setText("Нет дубликатов");
        } else {
            outputArea.setText(talker.getDuplicates());
        }
    }

    @FXML
    public void updateStats() {
        if ("Статистика".equals(tabPane.getSelectionModel().getSelectedItem().getText())) {
            statsPlacesArea.setText(talker.getStatsPlaces());
            statsDisksNum.setText(Integer.toString(talker.getDiskCount()));
            statsAvgCostLabel.setText(String.format("%.2f", talker.getAvgCost()));
            statsPopTypeLabel.setText(talker.getPopType());
            statsPopPubLabel.setText(talker.getPopPublisher());
            statsFilmNumLabel.setText(Integer.toString(talker.getFilmsNumber()));
            statsAudioNumLabel.setText(Integer.toString(talker.getAudioDiskNumber()));
            statsDocsNumLabel.setText(Integer.toString(talker.getDocDiskNumber()));
            statsSoftNumLabel.setText(Integer.toString(talker.getSoftDiskNumber()));
        }
    }
}
