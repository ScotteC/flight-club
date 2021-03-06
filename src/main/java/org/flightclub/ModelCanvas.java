/**
 This code is covered by the GNU General Public License
 detailed at http://www.gnu.org/copyleft/gpl.html

 Flight Club docs located at http://www.danb.dircon.co.uk/hg/hg.htm
 Dan Burton , Nov 2001
 */

package org.flightclub;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * canvas manager - draws world, dragging on canvas moves camera
 *
 * This class is based on the framework outlined in a book called
 * 'Java Games Programming' by Niel Bartlett
 */
public class ModelCanvas extends Canvas {
    public final Color backColor = Color.white;
    public final Image backImg = null;
    private Image imgBuffer;
    int width, height;
    protected ModelViewer app = null;
    boolean dragging = false;
    private Graphics graphicsBuffer;

    private int x0 = 0, y0 = 0;
    private int dx = 0, dy = 0;

    public ModelCanvas(ModelViewer theApp) {
        app = theApp;
    }

    void init() {
        width = getWidth();
        height = getHeight();

        imgBuffer = app.createImage(width, height);
        graphicsBuffer = imgBuffer.getGraphics();

        //event handlers
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
                y0 = e.getY();
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dx = 0;
                dy = 0;
                dragging = false;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                dx = e.getX() - x0;
                dy = e.getY() - y0;
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                app.eventManager.handleEvent(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                app.eventManager.handleEvent(e);
            }
        });
    }

    void tick() {
        if (dragging) {
            //float dtheta = (float) dx/width;
            float dtheta = 0;
            float unitStep = (float) Math.PI / (app.getFrameRate() * 8);//4 seconds to 90 - sloow!

            if (dx > 20) dtheta = -unitStep;
            if (dx < -20) dtheta = unitStep;

            app.cameraMan.rotateEyeAboutFocus(-dtheta, -dy);
        }
    }

    @Override
    public void paint(Graphics g) {
        if (imgBuffer == null) return;

        updateImgBuffer(graphicsBuffer);
        g.drawImage(imgBuffer, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void updateImgBuffer(Graphics g) {
        if (backImg == null) {
            g.setColor(backColor);
            g.fillRect(0, 0, width, height);
        } else
            g.drawImage(backImg, 0, 0, this);

        app.draw(g, width, height);
    }

}

