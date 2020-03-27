package com.taurus.racingTiming.controller;

import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImagemViewController extends FXMLBaseController implements IImagemViewController {

    private static final Log LOG = LogFactory.getLog(ImagemViewController.class);

    public ImagemViewController() {
        super();
    }

    @Parametro
    private Image imagem;

    @FXML
    private StackPane pane;
    @FXML
    private ImageView imageView;

    @Value("${fxml.cronometragemImagem.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pane.prefWidthProperty().bind(this.imageView.fitWidthProperty());
        this.pane.prefHeightProperty().bind(this.imageView.fitHeightProperty());
    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        this.imageView.setScaleY(1.0);
        this.imageView.setScaleX(1.0);
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public void setImagem(Image imagem) {
        this.imagem = imagem;
        this.imageView.setImage(imagem);
        //this.imageView.fitWidthProperty().bind(this.imagem.widthProperty());
        // this.imageView.fitHeightProperty().bind(this.imagem.heightProperty());
    }

    @FXML
    public void resizeImagem(ScrollEvent scrollEvent) {
        double fator = 0;
        if (scrollEvent.getDeltaY() > 0) {
            fator = 0.1;
        } else if (scrollEvent.getDeltaY() < 0) {
            fator = -0.1;
        }
        this.imageView.setScaleY(this.imageView.getScaleY() + fator);
        this.imageView.setScaleX(this.imageView.getScaleX() + fator);
    }

}
