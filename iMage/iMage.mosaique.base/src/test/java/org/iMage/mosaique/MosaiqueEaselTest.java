package org.iMage.mosaique;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.rectangle.RectangleArtist;
import org.junit.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MosaiqueEaselTest {

    private  MosaiqueEasel easel;
    private RectangleArtist artist;

    private Collection<BufferedArtImage> images;

    private BufferedArtImage image;
    private BufferedImage target;
    private File targetFile;


    @Before
    public void prepare() {
        easel = new MosaiqueEasel();
        images = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            final URL imageResource = this.getClass().getResource(String.format("/000%d.jpg", i));
            try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
                ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
                reader.setInput(iis, true);
                ImageReadParam params = reader.getDefaultReadParam();
                images.add(new BufferedArtImage(reader.read(0, params)));
                reader.dispose();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }

        final URL imageResource = this.getClass().getResource("/tichy_in.jpg");
        try (ImageInputStream iis = ImageIO.createImageInputStream(imageResource.openStream())) {
            ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
            reader.setInput(iis, true);
            ImageReadParam params = reader.getDefaultReadParam();
            image = new BufferedArtImage(reader.read(0, params));
            reader.dispose();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        artist = new RectangleArtist(images, image.getWidth() / 4, image.getHeight() / 4);
        targetFile = new File("target/test-classes/result.jpg");
        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createMosaiqueTest() {
        try {
            target = easel.createMosaique(image.toBufferedImage(), artist);
            ImageIO.write(target, "jpg", targetFile);
            assertTrue(targetFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void destroy() {
        image = null;
        target = null;
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }
}
