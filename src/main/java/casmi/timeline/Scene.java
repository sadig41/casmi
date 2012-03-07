/*
 *   casmi
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 *  casmi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package casmi.timeline;

import java.nio.DoubleBuffer;

import casmi.graphics.Graphics;
import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.element.Element;
import casmi.graphics.object.BackGround;
import casmi.graphics.object.Camera;
import casmi.graphics.object.Frustum;
import casmi.graphics.object.GraphicsObject;
import casmi.graphics.object.Light;
import casmi.graphics.object.Ortho;
import casmi.graphics.object.Perspective;

/**
 * Scene class for time line animation.
 * 
 * @author Y. Ban
 */
abstract public class Scene {

    private int id;
    private double time;
    private double sceneA = 1.0;
    private boolean selectionBuffer = false;

    private GraphicsObject rootObject = new GraphicsObject();

//	private static final int SELECTION_BUFSIZE = 512;
//    private int selectedIndex = 0;

    abstract public void setup();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getSceneA() {
        return sceneA;
    }

    public void setSceneA(double sceneA, Graphics g) {
        this.sceneA = sceneA;
        g.setSceneA(this.sceneA);
    }

    public void drawscene(Graphics g) {
      //  g.render(rootObject);
        rootObject.bufRender(g, 0, 0, false, 0);
        if (selectionBuffer) {}
        update(g);
    }

    public void update(Graphics g) {
        update();
    }

    public void update() {}

    public void setPosition(double x, double y, double z) {
        rootObject.setPosition(x, y, z);
    }

    public void setPosition(double x, double y) {
        rootObject.setPosition(x, y);
    }

    public void setRotation(double angle, double x, double y, double z) {
        rootObject.setRotation(angle, x, y, z);
    }

    public GraphicsObject getRootObject() {
        return rootObject;
    }

    public int addObject(Object r) {
        if (rootObject.getSize() == 0) {
            rootObject.add(r);
        } else {
            for (int i = 0; i < rootObject.getSize(); i++) {
                if (r.equals(rootObject.get(i)) && (r instanceof Element)) {
                    Element e = (Element)r;
                    Element ec = e.clone();
                    rootObject.add(ec);
                    return 0;
                }
            }
            rootObject.add(r);
        }
        return 0;
    }

    public void removeObject(int index) {
        rootObject.remove(index);
    }

    public int addObject(int index, Object r) {
        if (rootObject.getSize() == 0) {
            rootObject.add(index, r);
        } else {
            for (int i = 0; i < rootObject.getSize(); i++) {
                if (r.equals(rootObject.get(i)) && (r instanceof Element)) {
                    Element e = (Element)r;
                    Element ec = e.clone();
                    rootObject.add(index, ec);
                    return 0;
                }
            }
            rootObject.add(index, r);
        }
        return 0;
    }

    public Object getObject(int index) {
        return rootObject.get(index);
    }

    public void clearObject() {
        rootObject.clear();
    }

    public void setPerspective() {
        rootObject.addPerse(new Perspective());
    }

    public void setPerspective(double fov, double aspect, double zNear, double zFar) {
        rootObject.addPerse(new Perspective(fov, aspect, zNear, zFar));
    }

    public void setPerspective(Perspective perspective) {
        rootObject.addPerse(perspective);
    }

    public void setOrtho() {
        rootObject.addPerse(new Ortho());
    }

    public void setOrtho(double left, double right, double bottom, double top,
        double near, double far) {
        rootObject.addPerse(new Ortho(left, right, bottom, top, near, far));
    }

    public void setOrtho(Ortho ortho) {
        rootObject.addPerse(ortho);
    }

    public void setFrustum() {
        rootObject.addPerse(new Frustum());
    }

    public void setFrustum(double left, double right, double bottom, double top,
        double near, double far) {
        rootObject.addPerse(new Frustum(left, right, bottom, top, near, far));
    }

    public void setFrustum(Frustum frustum) {
        rootObject.addPerse(frustum);
    }

    public void setCamera() {
        rootObject.addCamera(new Camera());
    }

    public void setCamera(double eyeX, double eyeY, double eyeZ, double centerX,
        double centerY, double centerZ, double upX, double upY, double upZ) {
        rootObject.addCamera(new Camera(eyeX, eyeY, eyeZ, centerX,
            centerY, centerZ, upX, upY, upZ));
    }

    public void setCamera(Camera camera) {
        rootObject.addCamera(camera);
    }

    public void getCamera(int index) {
        rootObject.getCamera(index);
    }

    public void addLight(Light light) {
        rootObject.addLight(light);
    }

    public void getLight(int index) {
        rootObject.getLight(index);
    }

    public void addLight(int index, Light light) {
        rootObject.addLight(index, light);
    }

    public void removeLight(int index) {
        rootObject.remove(index);
    }

    public void applyMatrix(DoubleBuffer matrix) {
        rootObject.applyMatrix(matrix);
    }

    public void applyMatix(double matrix[]) {
        rootObject.applyMatrix(matrix);
    }

    public void loadMatrix(DoubleBuffer matrix) {
        rootObject.loadMatrix(matrix);
    }

    public void loadMatix(double matrix[]) {
        rootObject.loadMatrix(matrix);
    }

    public void setBackGroundColor(double gray) {
        rootObject.setBackGroundColor(new BackGround(gray));
    }

    public void setBackGroundColor(double r, double g, double b) {
        rootObject.setBackGroundColor(new BackGround(r, g, b));
    }

    public void setBackGroundColor(Color color) {
        rootObject.setBackGroundColor(new BackGround(color));
    }

    public void setBackGroundColor(ColorSet colorset) {
        rootObject.setBackGroundColor(new BackGround(colorset));
    }

    public void keyEvent(casmi.KeyEvent e) {

    }

    public void mouseEvent(casmi.MouseEvent e) {

    }

}