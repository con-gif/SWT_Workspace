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

/**
 * A class to define tests for the functionality of MosaiqueEasel
 *
 * @author Kaloyan Draganov
 * @version 0.0.1
 */
public class MosaiqueEaselTest {

    private  MosaiqueEasel easel;
    private RectangleArtist artist;

    private Collection<BufferedArtImage> images;

    private BufferedArtImage image;
    private BufferedImage target;
    private File targetFile;


    /**
     * Initializes objects for the tests.
     */
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

        artist = new RectangleArtist(images, image.getWidth() / MosaiqueEasel.REDUCTION_RATIO,
                image.getHeight() / MosaiqueEasel.REDUCTION_RATIO);
        targetFile = new File("target/test-classes/result.png");
        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests creating a mosaique version of a given image.
     */
    @Test
    public void createMosaiqueTest() {
        try {
            target = easel.createMosaique(image.toBufferedImage(), artist);
            ImageIO.write(target, "png", targetFile);
            assertTrue(targetFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Destroys test resources.
     */
    @After
    public void destroy() {
        easel = null;
        artist = null;

        image = null;
        target = null;
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }
}
