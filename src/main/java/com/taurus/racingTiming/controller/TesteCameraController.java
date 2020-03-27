package com.taurus.racingTiming.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;
import com.taurus.javafx.controller.FXMLBaseController;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.demos.DecodeAndPlayVideo;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TesteCameraController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(TesteCameraController.class);
    private Webcam webcam;
    private BufferedImage bufferedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private boolean stopCamera, gravar, primeiro;
    private Dimension dimensao;
    private long start;

    @FXML
    private ImageView imagem;
    @FXML
    private ImageView imagemFoto;
    @FXML
    private Label lblResolucao;
    @FXML
    private RadioButton vga;
    @FXML
    private RadioButton qvga;
    @FXML
    private RadioButton qqvga;
    @FXML
    private ComboBox<Webcam> comboCamera;

    public TesteCameraController() {
        super();
    }

    @Value("${fxml.testeCamera.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        this.comboCamera.getItems().clear();
        this.comboCamera.getItems().addAll(Webcam.getWebcams());
        if (!this.comboCamera.getItems().isEmpty()) {
            this.comboCamera.setValue(this.comboCamera.getItems().get(0));
        }

    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void iniciarCamera() {
        //webcam = Webcam.getDefault();
        webcam = this.comboCamera.getValue();
        this.abrirCamera();

        stopCamera = false;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((bufferedImage = webcam.getImage()) != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    final Image mainiamge = SwingFXUtils.toFXImage(bufferedImage, null);
                                    imageProperty.set(mainiamge);
                                }
                            });

                            bufferedImage.flush();
                        }
                    } catch (Exception e) {
                    } finally {
                    }
                }
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imagem.imageProperty().bind(imageProperty);
    }

    @FXML
    public void pararGravacao() {
        gravar = false;
        cj.exibirInformacao("video gravado.");
    }

    @FXML
    public void capturarImagem() {
        //BufferedImage bimage = webcam.getImage();
        //WritableImage wimg = SwingFXUtils.toFXImage(bimage, null);
        //imagemFoto.setImage(wimg);

        byte[] imagemByte = WebcamUtils.getImageBytes(webcam, ImageUtils.FORMAT_JPG);
        imagemFoto.setImage(new Image(new ByteArrayInputStream(imagemByte)));

    }

    @FXML
    public void fecharCamera() {
        if (webcam.isOpen()) {
            stopCamera = true;
            webcam.close();
        }
    }

    private void atualizarLabelResolucao() {
        this.lblResolucao.setText(WebcamResolution.SVGA.name() + " " + WebcamResolution.SVGA.getWidth() + " x " + WebcamResolution.SVGA.getHeight());
    }

    private void abrirCamera() {
        if (vga.isSelected()) {
            dimensao = WebcamResolution.VGA.getSize();
        } else if (qvga.isSelected()) {
            dimensao = WebcamResolution.QVGA.getSize();
        } else if (qqvga.isSelected()) {
            dimensao = WebcamResolution.QQVGA.getSize();
        }
        webcam.setViewSize(dimensao);
        webcam.open();
        this.atualizarLabelResolucao();

        for (WebcamResolution resolucao : WebcamResolution.values()) {
            System.out.println(resolucao.name() + ": " + resolucao.getWidth() + "x" + resolucao.getHeight());
        }
        System.out.println("\n\nResoluções possíveis:");
        for (Dimension d : webcam.getViewSizes()) {
            System.out.println(d.getWidth() + "x" + d.getHeight());
        }
    }

    //static Dimension screenBounds;
    public void gravar() {
        double FRAME_RATE = 25;
        final String outputFilename = "d:/video_" + dimensao.width + "_" + dimensao.height + "_" + FRAME_RATE + "r.mp4";

        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        //writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width / 2, screenBounds.height / 2);
        //int streamIndex = writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, dimensao.width, dimensao.height);
        int streamIndex = writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, dimensao.width, dimensao.height);

        long startTime = System.currentTimeMillis();
        this.gravar = true;
        Runnable r = () -> {
            while (gravar) {
                BufferedImage screen = bufferedImage;
                BufferedImage bgrScreen = ConverterFactory.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                //BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                IConverter converter = ConverterFactory.createConverter(bgrScreen, IPixelFormat.Type.YUV420P);
                IVideoPicture frame = converter.toPicture(bgrScreen, (System.currentTimeMillis() - start) * 1000);
                frame.setKeyFrame(primeiro);
                primeiro = false;
                frame.setQuality(0);
                writer.encodeVideo(streamIndex, frame);
                // sleep for frame rate milliseconds
                try {
                    Thread.sleep((long) (1000 / FRAME_RATE));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            writer.close();
        }; // tell the writer to close and write the trailer if  needed
        new Thread(r).start();

    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    @FXML
    public void videoAteInstante() {

    }

    @FXML
    public void gravarStream() {
        File outputFile = new File("/Teste.flv");

        if (outputFile.exists()) {
            outputFile.delete();
        }

//		open a container
        IContainer container = IContainer.make();
        container.open(outputFile.getAbsolutePath(), IContainer.Type.WRITE, null);

//		create the video stream and get its coder
        ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
        IStream videoStream = container.addNewStream(videoCodec);
        IStreamCoder videoStreamCoder = videoStream.getStreamCoder();

//		setup the stream coder
        IRational frameRate = IRational.make(1, 30);

        Integer OUTPUT_WIDTH = 960;
        Integer OUTPUT_HEIGHT = 540;

        videoStreamCoder.setWidth(dimensao.width);
        videoStreamCoder.setHeight(dimensao.height);
        videoStreamCoder.setFrameRate(frameRate);
        videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
                frameRate.getNumerator()));
        videoStreamCoder.setBitRate(350000);
        videoStreamCoder.setNumPicturesInGroupOfPictures(30);
        videoStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);
        videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        videoStreamCoder.setGlobalQuality(0);

//		open the coder first
        videoStreamCoder.open(null, null);

//		write the header
        container.writeHeader();

//		let us begin
//              encode 30 frames, right?
        this.gravar = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int i = 1;
                long start = System.currentTimeMillis();
                while (gravar) {
//			create a green box with a 50 pixel border for the frame image
//                    BufferedImage outputImage = new BufferedImage(videoStreamCoder.getWidth(),
//                            videoStreamCoder.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//                    Graphics graphics = outputImage.getGraphics();
//                    graphics.setColor(Color.GREEN);
//                    graphics.drawRect(50, 50,
//                            videoStreamCoder.getWidth() - 100,
//                            videoStreamCoder.getHeight() - 100);
                    BufferedImage screen = bufferedImage;
                    BufferedImage outputImage = ConverterFactory.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);

//			now, create a packet
                    IPacket packet = IPacket.make();

                    IConverter converter = ConverterFactory.createConverter(outputImage,
                            videoStreamCoder.getPixelType());
                    long positionInMicroseconds = (System.currentTimeMillis() - start) * 1000;

                    IVideoPicture frame = converter.toPicture(outputImage, positionInMicroseconds);
                    frame.setQuality(0);

                    if (videoStreamCoder.encodeVideo(packet, frame, 0) < 0) {
                        throw new RuntimeException("Unable to encode video.");
                    }

                    if (packet.isComplete()) {
                        if (container.writePacket(packet) < 0) {
                            throw new RuntimeException("Could not write packet to container.");
                        }
                    }
                    System.out.println("gravando..." + i++ + "   tempo: "+positionInMicroseconds);
                }
                container.writeTrailer();
                videoStreamCoder.close();
                container.flushPackets();
                container.close();
            }
        };
        new Thread(r).start();
//		done, so now let's wrap this up.		

        //DecodeAndPlayVideo.main(new String[]{outputFile.getAbsolutePath()});
    }
}
