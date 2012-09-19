/*   casmi examples
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package casmi.graphics.element;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.color.RGBColor;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Text class.
 * Wrap JOGL and make it easy to use.
 * 
 * @author  T. Takeuchi,Y. Ban
 * 
 */
public class TextBox extends Element implements Renderable , Reset{

    private static final boolean DEFAULT_FILL         = false;
    private static final Color   DEFAULT_FILL_COLOR   = new RGBColor(ColorSet.BLACK);
    private static final boolean DEFAULT_STROKE       = false;
    private static final Color   DEFAULT_STROKE_COLOR = new RGBColor(ColorSet.WHITE);
    
    private Text text;
    private double width = 0.0;
    private double height = 0.0;
    
    
    /**
     * Creates a new TextBox using Text, width and height.
     * 
     * @param text
     * 				The content of the TextBox.
     * @param width
     * 				The width of the TextBox.
     * @param height
     * 				The height of the TextBox.
     */  
    public TextBox(Text text, 
            double width, double height) {
 
    	this(text, 0.0, 0.0, 0.0, width, height);
    }
    
    /**
     * Creates a new TextBox using Text, x,y-coordinate, width and height.
     * 
     * @param text
     * 				The content of the TextBox.
     * @param x
     * 				The x-coordinate of the TextBox.
     * @param y
     * 				The y-coordinate of the TextBox.
     * @param width
     * 				The width of the TextBox.
     * @param height
     * 				The height of the TextBox.
     */
    public TextBox(Text text, 
                   double x, double y,
                   double width, double height) {
        
        this(text, x, y, 0.0, width, height);
    }
    
    /**
     * Creates a new TextBox using Text, x,y,z-coordinate, width and height.
     * 
     * @param text
     * 				The content of the TextBox.
     * @param x
     * 				The x-coordinate of the TextBox.
     * @param y
     * 				The y-coordinate of the TextBox.
     * @param z
     * 				The y-coordinate of the TextBox.
     * @param width
     * 				The width of the TextBox.
     * @param height
     * 				The height of the TextBox.
     */
    public TextBox(Text text,
                   double x, double y, double z, 
                   double width, double height) {
        
        this.text   = text;
        this.x      = x;
        this.y      = y;
        this.z      = z;
        this.width  = width;
        this.height = height;
        init();
        format();
        this.fill = true;
        this.stroke = true;
    }
    
    @Override
    public void render(GL2 gl, GLU glu, int width, int height) {

        double x1 =  - this.width  / 2.0;
        double y1 =  + this.height / 2.0;
        double x2 =  - this.width  / 2.0;
        double y2 =  - this.height / 2.0;
        double x3 =  + this.width  / 2.0;
        double y3 =  - this.height / 2.0;
        double x4 =  + this.width  / 2.0;
        double y4 =  + this.height / 2.0;
        
       // if (this.fillColor.getAlpha() < 0.001 || this.strokeColor.getAlpha() < 0.001 || this.isDepthTest()==false) {
            gl.glDisable(GL2.GL_DEPTH_TEST);
       // }

        this.setTweenParameter(gl);
        
        gl.glPushMatrix();
        {
            // fill
            if (fill) {
                getSceneFillColor().setup(gl);
                gl.glBegin(GL2.GL_QUADS);
                {
                    gl.glVertex2d(x1, y1);
                    gl.glVertex2d(x2, y2);
                    gl.glVertex2d(x3, y3);
                    gl.glVertex2d(x4, y4);
                }
                gl.glEnd();
            }
            
            // stroke
            if (stroke) {
                gl.glLineWidth(this.strokeWidth);
                getSceneStrokeColor().setup(gl);
                gl.glBegin(GL2.GL_LINE_STRIP);
                {
                    gl.glVertex2d(x1, y1);
                    gl.glVertex2d(x2, y2);
                    gl.glVertex2d(x3, y3);
                    gl.glVertex2d(x4, y4);
                    gl.glVertex2d(x1, y1);
                }
                gl.glEnd();
            }
            
            // text
            switch (text.getAlign()) {
            case CENTER:
                text.setX(0);
                break;
            case RIGHT:
                text.setX( + this.width / 2.0);
                break;
            case LEFT:
            default:
                text.setX( - this.width / 2.0);
                break;
            }
            text.setY( + this.height / 2.0 - text.getHeight());
            text.setZ(0);
            text.render(gl, glu, width, height);

            }
        gl.glPopMatrix();
        
      //  if (this.fillColor.getAlpha() < 0.001 || this.strokeColor.getAlpha() < 0.001 || this.isDepthTest()==false) {
            gl.glEnable(GL2.GL_DEPTH_TEST);
      //  }
    }
    
    private final void init() {
        this.fill        = DEFAULT_FILL;
        this.fillColor   = DEFAULT_FILL_COLOR;
        this.stroke      = DEFAULT_STROKE;
        this.strokeColor = DEFAULT_STROKE_COLOR;
    }
    
    private final void format() {
        
        String[] strs = text.getArrayText();
        TextRenderer tr = text.getRenderer();
        StringBuilder sb = new StringBuilder();
        try{
        for (String str : strs) {
        	//System.out.println("tes"+str);
        	//System.out.println("tes"+tr.getBounds(str).getWidth());
            while (1 < str.length() && width < tr.getBounds(str).getWidth()) {
                String tmp = str;
                while (1 < tmp.length() && width < tr.getBounds(tmp).getWidth()) {
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
                sb.append(tmp);
                sb.append('\n');
                str = str.substring(tmp.length());
            }
            sb.append(str);
            sb.append('\n');
        }
        } catch (GLException e) {
        	this.reset = true;
        }
        text.setArrayText(sb.toString());
    }

    public final Text getText() {
    
        return text;
    }

    public final void setText(Text text) {
    
        this.text = text;
        format();
    }

    public final double getX() {
    
        return x;
    }

    public final void setX(double x) {
    
        this.x = x;
    }

    public final double getY() {
    
        return y;
    }

    public final void setY(double y) {
    
        this.y = y;
    }

    public final double getZ() {
    
        return z;
    }

    public final void setZ(double z) {
    
        this.z = z;
    }

    public final double getWidth() {
    
        return width;
    }

    public final void setWidth(double width) {
    
        this.width = width;
    }

    public final double getHeight() {
    
        return height;
    }

    public final void setHeight(double height) {
    
        this.height = height;
    }

	@Override
	public void reset() {
		text.reset();
	}

}
