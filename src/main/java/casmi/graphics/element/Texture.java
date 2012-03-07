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

import java.net.URL;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import casmi.graphics.Graphics;
import casmi.image.Image;
import casmi.image.ImageMode;
import casmi.matrix.Vertex;

/**
 * Texture class.
 * Wrap JOGL and make it easy to use.
 * 
 * @author Y. Ban
 * 
 */
public class Texture extends Element implements Renderable {

    public static final int LINES     = 1;
    public static final int LINES_3D  = 3;
    public static final int LINE_LOOP = 51;
    
    public enum TextureFlipMode {Horizontal,Vertical};
    public enum TextureRotationMode {Half,FrontRight,BackRight};

    protected Image image;
    private Image mask;
    private Image maskedImage;
    
    protected double width;
    protected double height;
    protected double x;
    protected double y;
    protected double z;
    protected int    mode;
    
    
    private ArrayList<Double> xList;
    private ArrayList<Double> yList;
    private ArrayList<Double> zList;
    
    private ArrayList<Double> nx;
    private ArrayList<Double> ny;

    private Vertex tmpv = new Vertex();
    
    protected boolean reloadFlag = false;    

    protected boolean masking = false;
    
    private float[][] corner = {{0.0f,1.0f},{0.0f,0.0f},{1.0f,0.0f},{1.0f,1.0f}}; 

    public Texture(String path) {
        this(new Image(path));
    }

    public Texture(URL url) {
        this(new Image(url));
    }

    public Texture(Image image) {
        this.image = image;
        width  = image.getWidth();
        height = image.getHeight();

        xList = new ArrayList<Double>();
        yList = new ArrayList<Double>();
        zList = new ArrayList<Double>();
        nx = new ArrayList<Double>();
        ny = new ArrayList<Double>();

        reloadFlag = true;
    }
    
	public void setMask(String path){
		setMask(new Image(path));
	}
	
	public void setMask(URL url){
		setMask(new Image(url));
	}
	
	public void setMask(Image maskImage){
		masking = true;
		this.mask = maskImage;
		maskedImage = Image.clone(image);
		for(int imageY = 0; imageY<this.maskedImage.getHeight();imageY++){
			for(int imageX = 0; imageX<this.maskedImage.getWidth();imageX++){
				if(imageX<=mask.getWidth()&&imageY<=mask.getHeight())
					this.image.setA(mask.getGray(imageX, imageY), imageX , imageY);
			}
		}
		
	}
	
	public void setMask(Texture maskTexture){
		masking = true;
		this.mask = maskTexture.getImage();
		maskedImage = Image.clone(image);
		for(int imageY = 0; imageY<this.maskedImage.getHeight();imageY++){
			for(int imageX = 0; imageX<this.maskedImage.getWidth();imageX++){
				if(imageX<=mask.getWidth()&&imageY<=mask.getHeight())
					this.image.setA(mask.getGray(imageX, imageY), imageX , imageY);
			}
		}
		
	}
	
	public void setTexture(Texture texture){
		this.image = texture.image;
	}



    public void addVertex(double x, double y, double nx, double ny) {
        mode = LINES;
        this.xList.add(x);
        this.yList.add(y);
        this.nx.add(nx);
        this.ny.add(ny);
    }

    public void addVertex(double x, double y, double z, double nx, double ny) {
        mode = LINES_3D;
        this.xList.add(x);
        this.yList.add(y);
        this.zList.add(z);
        this.nx.add(nx);
        this.ny.add(ny);
    }

    public void addVertex(Vertex v, double nx, double ny) {
        mode = LINES_3D;
        this.xList.add(v.x);
        this.yList.add(v.y);
        this.zList.add(v.z);
        this.nx.add(nx);
        this.ny.add(ny);
    }

    public Vertex getVertex(int i) {
        tmpv.x = xList.get(i);
        tmpv.y = yList.get(i);
        tmpv.z = zList.get(i);
        return tmpv;
    }
    
    public Vertex getTextureVertex(int i){
    	tmpv.x = nx.get(i);
    	tmpv.y = ny.get(i);
    	return tmpv;
    }

    public void removeVertex(int i) {
        this.xList.remove(i);
        this.yList.remove(i);
        this.zList.remove(i);
        this.nx.remove(i);
        this.ny.remove(i);
    }

    public void setVertex(int i, double x, double y, double nx, double ny) {
        this.xList.set(i, x);
        this.yList.set(i, y);
        this.zList.set(i, 0d);
        this.nx.set(i, nx);
        this.ny.set(i, ny);
    }

    public void setVertex(int i, double x, double y, double z, double nx, double ny) {
        this.xList.set(i, x);
        this.yList.set(i, y);
        this.zList.set(i, z);
        this.nx.set(i, nx);
        this.ny.set(i, ny);
    }
    
    public void clearVertex(){
    	this.xList.clear();
    	this.yList.clear();
    	this.zList.clear();
    }

    public final void reload() {
        reloadFlag = true;
    }
    
    public final Image getImage() {
    	if(masking == false){
    		return this.image;
    	}
    	else{
    		return this.maskedImage;
    	}
    }

    public final void enableTexture() {
        image.enableTexture();
    }

    public final void disableTexture() {
        image.disableTexture();
    }

    @Override
    public void render(GL gl, GLU glu, int width, int height) {
        if (reloadFlag) {
            Graphics.reloadTextures();
            reloadFlag = false;
        }
        
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glPushMatrix();
        {
            this.setTweenParameter(gl);
            getSceneFillColor().setup(gl);
            double tmpx, tmpy, tmpz;
            double tmpnx, tmpny;
            image.enableTexture();
            material.setup(gl);
            if (xList.size() < 1) {
                Image img = getImage();
                gl.glBegin(GL.GL_QUADS);
                switch (img.getMode()) {
                default:
                case CORNER:
                    gl.glTexCoord2f(corner[0][0], corner[0][1]);
                    gl.glVertex2d(x, y - this.height * scaleY);
                    gl.glTexCoord2f(corner[1][0], corner[1][1]);
                    gl.glVertex2d(x, y);
                    gl.glTexCoord2f(corner[2][0], corner[2][1]);
                    gl.glVertex2d(x + this.width * scaleX, y);
                    gl.glTexCoord2f(corner[3][0], corner[3][1]);
                    gl.glVertex2d(x + this.width * scaleX, y - this.height * scaleY);
                    break;
                case CENTER:
                    gl.glTexCoord2f(corner[0][0], corner[0][1]);
                    gl.glVertex2d(x - this.width * scaleX / 2.0, y - this.height * scaleY / 2.0);
                    gl.glTexCoord2f(corner[1][0], corner[1][1]);
                    gl.glVertex2d(x - this.width * scaleX / 2.0, y + this.height * scaleY / 2.0);
                    gl.glTexCoord2f(corner[2][0], corner[2][1]);
                    gl.glVertex2d(x + this.width * scaleX / 2.0, y + this.height * scaleY / 2.0);
                    gl.glTexCoord2f(corner[3][0], corner[3][1]);
                    gl.glVertex2d(x + this.width * scaleX / 2.0, y - this.height * scaleY / 2.0);
                    break;
                }
                gl.glEnd();
            } else {
                switch (mode) {
                case LINES:
                    gl.glBegin(GL.GL_POLYGON);
                    for (int i = 0; i < xList.size(); i++) {
                        tmpx = this.xList.get(i);
                        tmpy = this.yList.get(i);
                        tmpnx = this.nx.get(i);
                        tmpny = this.ny.get(i);
                        gl.glTexCoord2d(tmpnx, tmpny);
                        gl.glVertex2d(tmpx, tmpy);
                    }
                    gl.glEnd();
                    break;
                case LINES_3D:
                    gl.glBegin(GL.GL_POLYGON);
                    for (int i = 0; i < xList.size(); i++) {
                        tmpx = this.xList.get(i);
                        tmpy = this.yList.get(i);
                        tmpz = this.zList.get(i);
                        tmpnx = this.nx.get(i);
                        tmpny = this.ny.get(i);
                        gl.glTexCoord2d(tmpnx, tmpny);
                        gl.glVertex3d(tmpx, tmpy, tmpz);
                    }
                    gl.glEnd();
                default:
                    break;
                }
            }
            image.disableTexture();
        }
        gl.glPopMatrix();
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public final double getWidth() {
        return width;
    }

    public final double getHeight() {
        return height;
    }

    public final void setWidth(double width) {
        this.width = width;
    }

    public final void setHeight(double height) {
        this.height = height;
    }

    public final void set(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width  = width;
        this.height = height;
    }

    public final void setMode(ImageMode mode) {
        image.setMode(mode);
    }
    
    public void getRGB(double x, double y){
    	
    }

    public final void setCorner(double x1, double y1, double x2, double y2) {
        this.x = (x1 + x2) / 2.0;
        this.y = (y1 + y2) / 2.0;
        this.width = Math.abs(x1 - x2);
        this.height = Math.abs(y1 - y2);
    }
    
	
	public void rotation(TextureRotationMode mode){
		float[][] tmp = (float[][])corner.clone();
		switch (mode) {
		case Half:
			corner[0] = tmp[2];
			corner[1] = tmp[3];
			corner[2] = tmp[0];
			corner[3] = tmp[1];
			break;
		case FrontRight:
			corner[0] = tmp[3];
			corner[1] = tmp[0];
			corner[2] = tmp[1];
			corner[3] = tmp[2];
			break;
		case BackRight:
			corner[0] = tmp[1];
			corner[1] = tmp[2];
			corner[2] = tmp[3];
			corner[3] = tmp[0];
			break;
		default:
			break;
		}
	}
	
	public void flip(TextureFlipMode mode){
		float[][] tmp = (float[][])corner.clone();
		switch (mode) {
		case Vertical:
			corner[0] = tmp[1];
			corner[1] = tmp[0];
			corner[2] = tmp[3];
			corner[3] = tmp[2];
			break;
		case Horizontal:
			corner[0] = tmp[3];
			corner[1] = tmp[2];
			corner[2] = tmp[1];
			corner[3] = tmp[0];
			break;
		default:
			break;
		}
	}
	
	public void setTextureCorner(int index, double x,double y){
		corner[index][0] = (float)x;
		corner[index][1] = (float)y;
	}
	
	public float getTextureCorner(int index1,int index2){
		return corner[index1][index2];
	}
	

	
	public void enableMask(){
		masking = true;
	}
	
	public void disableMask(){
		masking = false;
	}
	
	
}