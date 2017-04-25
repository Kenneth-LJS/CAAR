package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphUtils {

    public static final int IMG_HEIGHT = 501;
    public static final int COLOR_GRAPH_BG = Color.WHITE.getRGB();
    public static final int COLOR_GRAPH_MIDDLE_LINE = Color.BLUE.getRGB();
    public static final int COLOR_GRAPH_FILL = Color.BLACK.getRGB();
    
    public static void dataToGraph(float[] vals, String fileName) throws IOException {
        int imgWidth = vals.length;
        BufferedImage img = new BufferedImage(imgWidth, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < IMG_HEIGHT; y++) {
                img.setRGB(x, y, COLOR_GRAPH_BG);
            }
        }
        int midHeight = IMG_HEIGHT / 2;
        for (int x = 0; x < imgWidth; x++) {
            img.setRGB(x, midHeight, COLOR_GRAPH_MIDDLE_LINE);
        }
        float max = Float.MIN_VALUE;
        for (int x = 0; x < vals.length; x++) {
            max = Math.max(max, Math.abs(vals[x]));
        }
        for (int x = 0; x < vals.length; x++) {
            int curY = (int)Math.ceil(Math.abs(vals[x]) * (midHeight - 2) / max);
            if (vals[x] > 0) {
                curY = (midHeight - 1) - curY;
                for (int y = curY; y < midHeight; y++) {
                    img.setRGB(x, y, COLOR_GRAPH_FILL);
                }
            } else if (vals[x] < 0) {
                curY = (midHeight + 1) + curY;
                for (int y = midHeight + 1; y <= curY; y++) {
                    img.setRGB(x, y, COLOR_GRAPH_FILL);
                }
            }
        }
        ImageIO.write(img, "bmp", new File(fileName));
    }    
}
