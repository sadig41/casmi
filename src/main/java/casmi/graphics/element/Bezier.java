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
  
package casmi.graphics.element;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.color.RGBColor;
import casmi.matrix.Vertex;

/**
 * Bezier class.
 * Wrap JOGL and make it easy to use.
 * 
 * @author Y. Ban
 * 
 */
public class Bezier extends Element implements Renderable {

    private double points[] = {
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
    };

    private int detail = 30;
    
    private Color startColor;
    private Color endColor;
    private Color gradationColor = new RGBColor(0.0, 0.0, 0.0);

    /**
     * Creates a new Bezier object using coordinates for the anchor and control points.
     * 
     * @param x1,y1
     *            The coordinates for the first anchor point.
     * @param x2,y2
     *            The coordinates for the first control point.
     * @param x3,y3
     *            The coordinates for the second control point.
     * @param x4,y4
     *            The coordinates for the second ancor point.
     */
    public Bezier(double x1, double y1, 
                  double x2, double y2,
                  double x3, double y3,
                  double x4, double y4) {
        this(x1, y1, 0.0,
             x2, y2, 0.0,
             x3, y3, 0.0,
             x4, y4, 0.0);
    }

    /**
     * Creates a new Bezier object using coordinates for the anchor and control points.
     * 
     * @param x1,y1,z1
     *            The coordinates for the first anchor point.
     * @param x2,y2,z2
     *            The coordinates for the first control point.
     * @param x3,y3,z3
     *            The coordinates for the second control point.
     * @param x4,y4,z4
     *            The coordinates for the second ancor point.
     */
    public Bezier(double x1, double y1, double z1, 
                  double x2, double y2, double z2,
                  double x3, double y3, double z3,
                  double x4, double y4, double z4) {
        this.points[0]  = x1;
        this.points[1]  = y1;
        this.points[2]  = z1;
        this.points[3]  = x2;
        this.points[4]  = y2;
        this.points[5]  = z2;
        this.points[6]  = x3;
        this.points[7]  = y3;
        this.points[8]  = z3;
        this.points[9]  = x4;
        this.points[10] = y4;
        this.points[11] = z4;
        set();
    }
    
    private final void set() {
        x = this.points[0];
        y = this.points[1];
        z = this.points[2];
    }
    
    /**
     * Creates a new Bezier object using coordinates for the anchor and control points.
     * 
     * @param v1
     *            The coordinates for the first anchor point.
     * @param v2
     *            The coordinates for the first control point.
     * @param v3
     *            The coordinates for the second control point.
     * @param v4
     *            The coordinates for the second ancor point.
     */
    public Bezier(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        this(v1.x, v1.y, v1.z,
             v2.x, v2.y, v2.z,
             v3.x, v3.y, v3.z,
             v4.x, v4.y, v4.z);
    }
    
    public void setNode(int number, double x, double y) {
        setNode(number, x, y, 0.0);
    }

    public void setNode(int number, double x, double y, double z) {
        if (number <= 0)
            number = 0;
        if (number >= 3)
            number = 3;
        this.points[number * 3]     = x;
        this.points[number * 3 + 1] = y;
        this.points[number * 3 + 2] = z;
        set();
    }

    public void setNode(int number, Vertex v) {
        setNode(number, v.x, v.y, v.z);
    }

    @Override
    public void render(GL gl, GLU glu, int width, int height) {
        if (this.fillColor.getAlpha() != 1.0 || this.strokeColor.getAlpha() != 1.0) {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }

        gl.glPushMatrix();
        {
            this.setTweenParameter(gl);
            gl.glTranslated(-this.points[0], -this.points[1], -this.points[2]);
            
            if (this.fill) {
                getSceneFillColor().setup(gl);

                gl.glMap1d(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, java.nio.DoubleBuffer.wrap(points));
                gl.glEnable(GL.GL_MAP1_VERTEX_3);
                gl.glBegin(GL.GL_TRIANGLE_FAN);

                for (int i = 0; i <= detail; i++) {
                    if (i == 0 && isGradation() == true && startColor != null)
                        getSceneColor(this.startColor).setup(gl);
                    if (i == detail && isGradation() == true && endColor != null)
                        getSceneColor(this.endColor).setup(gl);
                    if (i != 0 && i != detail && isGradation() && endColor != null && startColor != null) {
                        gradationColor = RGBColor.lerpColor(this.startColor, this.endColor, (i / (double)detail));
                        getSceneColor(this.gradationColor).setup(gl);
                    }
                    gl.glEvalCoord1f((float)(i / (float)detail));
                }

                gl.glEnd();
                gl.glDisable(GL.GL_MAP1_VERTEX_3);
            }

            if (this.stroke) {
                getSceneStrokeColor().setup(gl);

                gl.glMap1d(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, java.nio.DoubleBuffer.wrap(points));
                gl.glEnable(GL.GL_MAP1_VERTEX_3);
                gl.glBegin(GL.GL_LINE_STRIP);

                for (int i = 0; i <= detail; i++) {
                    if (i == 0 && isGradation() && startColor != null)
                        getSceneColor(this.startColor).setup(gl);
                    if (i == detail && isGradation() && endColor != null)
                        getSceneColor(this.endColor).setup(gl);
                    if (i != 0 && i != detail && isGradation() && endColor != null && startColor != null) {
                        gradationColor = RGBColor.lerpColor(this.startColor, this.endColor, (i / (double)detail));
                        getSceneColor(this.gradationColor).setup(gl);
                    }
                    gl.glEvalCoord1f((float)(i / (float)detail));
                }
                gl.glEnd();
                gl.glDisable(GL.GL_MAP1_VERTEX_3);
            }

            gl.glTranslated(this.points[0], this.points[1], this.points[2]);
        }
        gl.glPopMatrix();
        
        if (this.fillColor.getAlpha() != 1.0 || this.strokeColor.getAlpha() != 1.0) {
            gl.glEnable(GL.GL_DEPTH_TEST);
        }
    }
    
    /**
     * Returns the detail of this Bezier.
     */
    public int getDetail() {
    	return detail;
    }
    
    /**
     * Set the detail of this Bezier.
     * 
     * @param detail
     *             The detail of the Bezier.
     */
    public void setDetail(int d) {
        detail = d;
    }
    
    public void setAnchorColor(int index, Color color) {
        if (index == 0) {
            if (startColor == null) {
                startColor = new RGBColor(0.0, 0.0, 0.0);
            }
            setGradation(true);
            this.startColor = color;
        } else if (index == 1) {
            if (endColor == null) {
                endColor = new RGBColor(0.0, 0.0, 0.0);
            }
            setGradation(true);
            this.endColor = color;
        }
    }
    
    public void setAnchorColor(int index, ColorSet colorSet) {
        setAnchorColor(index, new RGBColor(colorSet));
    }
}