package de.hdskins.skinrenderer;

import de.hdskins.skinrenderer.render.TextureType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LegacyFormatter {

    // I had no idea how to fix that the legacy skins aren't rendered properly, so just convert them to a non-legacy skin


    public static BufferedImage asNonLegacy(BufferedImage legacy) {
        BufferedImage image = new BufferedImage(legacy.getWidth(), legacy.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        float factor = (float) (legacy.getWidth() / 64D);

        g.drawImage(legacy, 0, 0, null);
        for (int d = 0; d < 6; d++) {
            copy(d * 4, factor, TextureType.RLEG, TextureType.LLEG, image, g);
            copy(d * 4, factor, TextureType.RARM, TextureType.LARM, image, g);
        }

        g.dispose();
        return image;
    }

    private static void copy(int d, float factor, TextureType src, TextureType dst, BufferedImage image, Graphics2D g) {
        // d = 0 -> front
        // d = 1 -> back
        // ... see comments in the TextureType enum

        g.drawImage(image,
                (int) (dst.rawCoords[d] * factor), (int) (dst.rawCoords[d + 1] * factor), (int) ((dst.rawCoords[d] + dst.rawCoords[d + 2]) * factor), (int) ((dst.rawCoords[d + 1] + dst.rawCoords[d + 3]) * factor),
                (int) (src.rawCoords[d] * factor), (int) (src.rawCoords[d + 1] * factor), (int) ((src.rawCoords[d] + src.rawCoords[d + 2]) * factor), (int) ((src.rawCoords[d + 1] + src.rawCoords[d + 3]) * factor),
                null
        );
    }

}
