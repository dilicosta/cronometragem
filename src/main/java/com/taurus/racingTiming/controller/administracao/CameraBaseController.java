package com.taurus.racingTiming.controller.administracao;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.controller.IImagemViewController;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.ContextUtil;
import com.taurus.util.FormatarUtil;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CameraBaseController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(CameraBaseController.class);

    private final int FRAME_RATE = 25;
    private Webcam webcam;
    private Dimension dimensaoVideo;
    private BufferedImage bufferedImagemVideo;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    protected SimpleBooleanProperty recProperty = new SimpleBooleanProperty(false);
    protected SimpleBooleanProperty cameraLigadaProperty = new SimpleBooleanProperty(false);
    private boolean gravacaoPausada = false;

    @Autowired
    private IImagemViewController imagemViewController;

    public CameraBaseController() {
        super();
    }

    @FXML
    private Rectangle imagemPreta;
    @FXML
    private ImageView previewCam;
    @FXML
    private Button btnCamOnOff;
    @FXML
    private Button btnCamRec;
    @FXML
    private Button btnCamPause;
    @FXML
    private Button btnCamStopRec;
    @FXML
    private ImageView imagemRec;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.configurarVisualizacaoComponentesCamera();
    }

    @FXML
    public void ligarDesligarCamera() {
        if (this.cameraLigadaProperty.get()) {
            this.desligarCamera();
        } else {
            this.ligarCamera();
        }
    }

    @FXML
    public void gravarVideo() {
        this.recProperty.set(true);
        this.gravacaoPausada = false;
        this.btnCamRec.setDisable(true);
        this.imagemRec.setImage(new Image(this.getClass().getResource(ContextUtil.getAppContext().getEnvironment().getProperty("img.bolinhaRec")).toExternalForm()));

        final File file = this.criarArquivoVideo();
        final IMediaWriter writer = ToolFactory.makeWriter(file.getAbsolutePath());
        final int streamIndex = writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, this.webcam.getViewSize().width, this.webcam.getViewSize().height);

        Runnable r = () -> {
            boolean primeiroFrame = true, primeiroCicloPause = true;
            final long frameRateMilisegundos = 1000 / FRAME_RATE;
            long tempoInicio = System.currentTimeMillis();
            long tempoPause = 0;
            long tempoAnterior = 0;
            int contadorFlush = 0;
            boolean continuarGravando = false;

            while (this.recProperty.get()) {
                long tempoAgora = System.currentTimeMillis();
                if (!this.gravacaoPausada) {
                    primeiroCicloPause = true;
                    BufferedImage screen = this.inserirDataImagem(this.bufferedImagemVideo);
                    BufferedImage bgrScreen = ConverterFactory.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                    IConverter converter = ConverterFactory.createConverter(bgrScreen, IPixelFormat.Type.YUV420P);
                    long tempoVideo = (tempoAgora - tempoInicio - tempoPause) * 1000;
                    IVideoPicture frame = converter.toPicture(bgrScreen, tempoVideo);
                    frame.setKeyFrame(primeiroFrame);
                    primeiroFrame = false;
                    frame.setQuality(0);
                    writer.encodeVideo(streamIndex, frame);
                    contadorFlush++;
                    // 5 minutos, salva e comeca a gravar outro video
                    if (tempoVideo > 300 * 1000000) {
                        pararGravacaoVideo();
                        continuarGravando = true;
                    }
                } else {
                    tempoPause += tempoAgora - tempoAnterior;
                }
                tempoAnterior = tempoAgora;
                try {
                    Thread.sleep(frameRateMilisegundos);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
            writer.flush();
            writer.close();
            // Recurso utilizado para fazer varios videos
            if (continuarGravando) {
                gravarVideo();
            }
        };
        new Thread(r).start();
    }

    @FXML
    public void pausarVideo() {
        this.gravacaoPausada = !this.gravacaoPausada;
        if (gravacaoPausada) {
            this.imagemRec.setImage(new Image(this.getClass().getResource(ContextUtil.getAppContext().getEnvironment().getProperty("img.pauseRec")).toExternalForm()));
        } else {
            this.imagemRec.setImage(new Image(this.getClass().getResource(ContextUtil.getAppContext().getEnvironment().getProperty("img.bolinhaRec")).toExternalForm()));
        }
    }

    @FXML
    public void pararGravacaoVideo() {
        this.recProperty.set(false);
        this.btnCamRec.setDisable(false);
        this.gravacaoPausada = false;
    }

    private void ligarCamera() {
        if (this.configuracoesCameraOk()) {

            if (this.webcam.isOpen()) {
                super.cj.exibirAviso("A camêra está em uso por outra aplicação ou funcionalidade.");
                return;
            }
            this.webcam.setViewSize(dimensaoVideo);
            this.cameraLigadaProperty.setValue(this.webcam.open());
            if (this.cameraLigadaProperty.get()) {
                this.removerTooltipEstiloBotao(this.btnCamOnOff);
                this.btnCamRec.setDisable(false);
                this.btnCamOnOff.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_OFF.getValor());
                this.btnCamOnOff.setTooltip(new Tooltip("desligar câmera"));

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while (cameraLigadaProperty.get()) {
                            try {
                                if ((bufferedImagemVideo = webcam.getImage()) != null) {
//                                    Platform.runLater(new Runnable() {
//                                        @Override
//                                        public void run() {
                                            final Image mainiamge = SwingFXUtils.toFXImage(bufferedImagemVideo, null);
                                            imageProperty.set(mainiamge);
//                                        }
//                                    });
                                    bufferedImagemVideo.flush();
                                }
                            } catch (Exception ex) {
                                LOG.error("Erro ao obter os frames da câmera para o preview.", ex);
                            }
                            Thread.sleep((long) 1000 / FRAME_RATE);
                        }
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
                                imageProperty.set(null);
//                            }
//                        });
                        return null;
                    }
                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        }
    }

    public void desligarCamera() {
        if (this.cameraLigadaProperty.get()) {
            if (this.recProperty.get()) {
                this.pararGravacaoVideo();
            }

            this.cameraLigadaProperty.set(false);
            this.btnCamRec.setDisable(true);
            this.removerTooltipEstiloBotao(this.btnCamOnOff);
            this.btnCamOnOff.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_ON.getValor());
            this.btnCamOnOff.setTooltip(new Tooltip("ligar câmera"));

            if (this.webcam != null && this.webcam.isOpen()) {
                this.webcam.close();
            }
        }
    }

    private void configurarVisualizacaoComponentesCamera() {
        this.previewCam.imageProperty().bind(this.imageProperty);
        this.previewCam.visibleProperty().bind(this.imageProperty.isNotNull());
        this.imagemPreta.visibleProperty().bind(this.imageProperty.isNull());
        this.btnCamRec.setDisable(true);
        this.imagemRec.visibleProperty().bind(this.recProperty);
        this.btnCamPause.disableProperty().bind(this.recProperty.not());
        this.btnCamStopRec.disableProperty().bind(this.recProperty.not());

        this.btnCamOnOff.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_ON.getValor());
        this.btnCamOnOff.setTooltip(new Tooltip("ligar câmera"));
        this.btnCamRec.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_REC.getValor());
        this.btnCamRec.setTooltip(new Tooltip("gravar vídeo"));
        this.btnCamPause.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PAUSE.getValor());
        this.btnCamPause.setTooltip(new Tooltip("pausar gravação"));
        this.btnCamStopRec.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_STOP.getValor());
        this.btnCamStopRec.setTooltip(new Tooltip("parar gravação"));
    }

    private void removerTooltipEstiloBotao(Button botao) {
        botao.getStyleClass().clear();
        botao.setTooltip(null);
    }

    private String criarNomeVideo() {
        LocalDateTime d = LocalDateTime.now();
        StringBuilder nome = new StringBuilder();
        nome.append("v_");
        nome.append(d.getYear()).append("-");
        nome.append(FormatarUtil.lpad(d.getMonthValue(), 2)).append("-");
        nome.append(FormatarUtil.lpad(d.getDayOfMonth(), 2)).append("-");
        nome.append(FormatarUtil.lpad(d.getHour(), 2)).append("_");
        nome.append(FormatarUtil.lpad(d.getHour(), 2)).append("-");
        nome.append(FormatarUtil.lpad(d.getMinute(), 2)).append(".mp4");
        return nome.toString();
    }

    protected abstract String getPath();

    private File criarArquivoVideo() {
        File file = new File(this.getPath() + this.criarNomeVideo());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    private BufferedImage inserirDataImagem(BufferedImage old) {

        BufferedImage img = new BufferedImage(old.getWidth(), old.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        //g2d.drawImage(old, 0, 0, w, h, this);
        g2d.drawImage(old, null, 0, 0);
        g2d.setPaint(Color.YELLOW);
        g2d.setFont(new Font("Serif", Font.BOLD, 12));
        String dataHora = FormatarUtil.localDateTimeToString(LocalDateTime.now(), FormatarUtil.FORMATO_DATA_HORA_SEG);
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth() - fm.stringWidth(dataHora) - 5;
        int y = img.getHeight() - fm.getHeight();
        g2d.drawString(dataHora, x, y);
        g2d.dispose();
        return img;
    }

    protected byte[] capturarImagem() {
        if (this.cameraLigadaProperty.get() && this.webcam.isOpen()) {
            return WebcamUtils.getImageBytes(this.webcam, ImageUtils.FORMAT_JPG);
        } else {
            return null;
        }
    }

    protected void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }

    protected void setResolucao(Dimension dimension) {
        this.dimensaoVideo = dimension;
    }

    private boolean configuracoesCameraOk() {
        if (this.webcam == null) {
            super.cj.exibirErro("Câmera de vídeo não configurada");
            return false;
        }
        if (this.dimensaoVideo == null) {
            super.cj.exibirErro("Resolução da câmera de vídeo não configurada");
            return false;
        }

        for (Dimension d : this.webcam.getViewSizes()) {
            if (d.equals(this.dimensaoVideo)) {
                return true;
            }
        }
        super.cj.exibirErro("A resolução configurada [" + this.dimensaoVideo.width + " x " + this.dimensaoVideo.height + "] não é compatível com a câmerada configurada.");
        return false;
    }
}
