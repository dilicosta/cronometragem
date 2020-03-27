package com.taurus.racingTiming.controller;

import javafx.scene.Node;
import javafx.stage.Stage;

public interface IPrincipalController {

    public void setPrimaryStage(Stage primaryStage);

    public Node getView();
    
    public void aoFecharJanela();
}
