package com.networking.clientserverchatapp;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML private TextField uname_choice;
    @FXML private Button begin_chat;
    @FXML private AnchorPane ap_chat;
    @FXML private VBox msg_history;
    @FXML private ScrollPane scroll_pane;
    @FXML private TextField message_to_send;
    @FXML private Button send_button;

    private static Client client;

    public void beginChat(ActionEvent event) throws IOException {
        String username = uname_choice.getText();

        if (!username.isEmpty()) {
            Socket socket = new Socket("localhost", 1234);
            client = new Client(socket, username);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setUserData(username);
            Scene scene = new Scene(root, 337, 333);
            stage.setTitle("Chat");
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (msg_history != null) {
            client.listenForMessage(msg_history);

            msg_history.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    scroll_pane.setVvalue((Double) newValue);
                }
            });
        }
    }

    public void sendMessage(ActionEvent event) {
        String message = message_to_send.getText();

        if (!message.isEmpty()) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.setPadding(new Insets(5, 5, 5, 10));

            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle("-fx-color: rgb(239, 242, 255); " +
                    "-fx-background-color: rgb(15, 125, 242); " +
                    "-fx-background-radius: 20px;");
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.996));

            hbox.getChildren().add(textFlow);

            msg_history.getChildren().add(hbox);

            client.sendMessage(message);
            message_to_send.clear();
        }
    }

    public static void addLabel(String message, VBox vbox) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 233); " +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hbox);
            }
        });
    }
}