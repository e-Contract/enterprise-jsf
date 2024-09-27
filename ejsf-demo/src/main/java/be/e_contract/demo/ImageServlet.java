/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/demo/image/*")
public class ImageServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServlet.class);

    private enum ImageQuality {
        HD, NORMAL, THUMBNAIL
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //response.setHeader("Cache-Control", "public, max-age=" + 60 * 60 * 24 * 30 + ";");
        response.setHeader("Cache-Control", "no-store;");

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String parameters = requestUri.substring(contextPath.length() + "/demo/image/".length());
        LOGGER.debug("image: {}", parameters);

        ImageQuality imageQuality;
        if (parameters.startsWith("hd/")) {
            imageQuality = ImageQuality.HD;
            parameters = parameters.substring("hd/".length());
        } else if (parameters.startsWith("thumbnail/")) {
            imageQuality = ImageQuality.THUMBNAIL;
            parameters = parameters.substring("thumbnail/".length());
        } else {
            imageQuality = ImageQuality.NORMAL;
        }

        long mediaId;
        try {
            mediaId = Long.parseLong(parameters);
        } catch (NumberFormatException e) {
            outputEmptyImage(response);
            return;
        }

        Random random = new Random(mediaId);
        //int width = 1024 + random.nextInt(1024);
        //int height = 1024 + random.nextInt(1024);
        int width = 1920;
        int height = 1080;

        BufferedImage photo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) photo.getGraphics();
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(renderingHints);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        for (int i = 0; i < 100; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            Color color = new Color(r, g, b);
            graphics.setColor(color);
            graphics.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int w = random.nextInt(200);
            int h = random.nextInt(200);
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            Color color = new Color(r, g, b);
            graphics.setColor(color);
            graphics.drawOval(x, y, w, h);
        }

        graphics.setColor(Color.BLACK);
        Font currentFont = graphics.getFont();
        Font newFont = currentFont.deriveFont(Font.PLAIN, 50);
        graphics.setFont(newFont);
        graphics.drawString(Long.toString(mediaId), 0, 50);

        graphics.dispose();

        response.setContentType("image/jpg");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.error("sleep error");
        }

        switch (imageQuality) {
            case HD:
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    Thumbnails.of(photo)
                            .size(1920, 1080)
                            .outputFormat("JPEG")
                            .outputQuality(1.0d)
                            .antialiasing(Antialiasing.ON)
                            .toOutputStream(outputStream);
                }
                break;
            case NORMAL:
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    Thumbnails.of(photo)
                            .size(800, 600)
                            .outputFormat("JPEG")
                            .outputQuality(1.0d)
                            .antialiasing(Antialiasing.ON)
                            .toOutputStream(outputStream);
                }
                break;
            case THUMBNAIL:
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    Thumbnails.of(photo)
                            .size(80, 60)
                            .outputFormat("JPEG")
                            .outputQuality(1.0d)
                            .antialiasing(Antialiasing.ON)
                            .toOutputStream(outputStream);
                }
                break;
        }
    }

    private void outputEmptyImage(HttpServletResponse response) throws IOException {
        BufferedImage photo = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) photo.getGraphics();
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(renderingHints);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 1, 1);
        graphics.dispose();

        response.setContentType("image/jpg");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(photo, "jpg", outputStream);
        }
    }
}
