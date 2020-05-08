package org.iMage.mosaique.rectangle;

import org.iMage.mosaique.base.BufferedArtImage;
import org.junit.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RectangleArtistTest {

    private RectangleArtist rectangleArtist;
    private RectangleArtist rectangleArtistBig;


    private Collection<BufferedArtImage> images;
    private Collection<BufferedArtImage> imagesBig;

    private BufferedArtImage image;

    @Before
    public void prepare() {
        images = new ArrayList<>();
        imagesBig = new ArrayList<>();

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
        rectangleArtist = new RectangleArtist(images, image.getWidth() / 4, image.getHeight() / 4);

        for (int i = 0; i < 100; i++) {
            imagesBig.addAll(images);
        }
        Collections.shuffle((List<?>) imagesBig);
        rectangleArtistBig = new RectangleArtist(imagesBig, image.getWidth() / 4, image.getHeight() / 4);
    }

    @Test
    public void testGetTileForRegion() {
        BufferedArtImage tile = rectangleArtist.getTileForRegion(image);
        assertTrue(tile.getHeight() > 0);
    }

    @Test(timeout = 1000)
    public void testGetTileForRegionTimed() {
        BufferedArtImage tile = rectangleArtistBig.getTileForRegion(image);
        assertTrue(tile.getHeight() > 0);
    }

    @After
    public void destroy() {
        image = null;
        images = null;
        imagesBig = null;

        rectangleArtist = null;
        rectangleArtistBig = null;
    }
}
