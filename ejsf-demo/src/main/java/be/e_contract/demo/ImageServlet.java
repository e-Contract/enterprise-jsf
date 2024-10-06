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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    private enum ImageDimension {

        ID_800_600(800, 600),
        ID_640_480(640, 480),
        HDTV(1920, 1080),
        HD(1280, 720),
        SOCIAL(1080, 1080),
        PHONE(1080, 720),
        HUGE(2048, 1536),
        ID_1366_768(1366, 768);

        private final int width;
        private final int height;

        private ImageDimension(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }

    private static final int DEFAULT_DELAY = 1000;

    private static final String DELAY_SESSION_ATTRIBUTE = ImageServlet.class.getName() + ".delay";

    private static final String VARIABLE_DIMENSIONS_SESSION_ATTRIBUTE = ImageServlet.class.getName() + ".variableDimensions";

    public static void setDelay(int delay) {
        HttpSession httpSession = getHttpSession();
        httpSession.setAttribute(DELAY_SESSION_ATTRIBUTE, delay);
    }

    private int getDelay(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Integer delay = (Integer) httpSession.getAttribute(DELAY_SESSION_ATTRIBUTE);
        if (null == delay) {
            delay = DEFAULT_DELAY;
        }
        return delay;
    }

    private static HttpSession getHttpSession() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpSession httpSession = request.getSession();
        return httpSession;
    }

    public static void setVariableDimensions(boolean variableDimensions) {
        HttpSession httpSession = getHttpSession();
        httpSession.setAttribute(VARIABLE_DIMENSIONS_SESSION_ATTRIBUTE, variableDimensions);
    }

    private boolean isVariableDimensions(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Boolean variableDimensions = (Boolean) httpSession.getAttribute(VARIABLE_DIMENSIONS_SESSION_ATTRIBUTE);
        if (null == variableDimensions) {
            variableDimensions = false;
        }
        return variableDimensions;
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

        int width;
        int height;
        Random random = new Random(mediaId);
        random.nextInt();
        boolean variableDimensions = isVariableDimensions(request);
        if (variableDimensions) {
            int idx = random.nextInt(ImageDimension.values().length);
            ImageDimension imageDimension = ImageDimension.values()[idx];
            boolean flip = random.nextBoolean();
            if (flip) {
                height = imageDimension.getWidth();
                width = imageDimension.getHeight();
            } else {
                width = imageDimension.getWidth();
                height = imageDimension.getHeight();
            }
        } else {
            ImageDimension imageDimension = ImageDimension.HDTV;
            width = imageDimension.getWidth();
            height = imageDimension.getHeight();
        }

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

        int delay = getDelay(request);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                LOGGER.error("sleep error");
            }
        }

        switch (imageQuality) {
            case HD:
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    ImageIO.write(photo, "jpg", outputStream);
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
