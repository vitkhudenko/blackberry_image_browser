package com.cleverua.bb.imagebrowser.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;

import com.cleverua.bb.imagebrowser.BitmapLoader;
import com.cleverua.bb.imagebrowser.IBitmapLoading;
import com.cleverua.bb.imagebrowser.IImageBrowserItemModel;

public class ImageBrowserItemField extends Field {

    private static final String READING_LABEL = "Reading...";
    private static final String ERROR_LABEL   = "Error";
    
    private static final int PADDING_LEFT   = 5;
    private static final int PADDING_RIGHT  = 6;
    private static final int PADDING_TOP    = 5;
    private static final int PADDING_BOTTOM = 6;
    private static final int TOTAL_PADDING_X = PADDING_RIGHT + PADDING_LEFT;
    private static final int TOTAL_PADDING_Y = PADDING_TOP + PADDING_BOTTOM;
    
    private int w; // Field's width
    private int h; // Field's height
    
    private String imgUrl;
    private int desiredImgHeight;
    private int desiredImgWidth;
    private Bitmap bmp;
    private boolean isLoadingImage;
    private boolean errorLoadingImage;
    
    private String label;
    private int labelHeight;
    private int labelWidth;
    
    private int readingHeight;
    private int readingWidth;
    
    private int errorLoadingWidth;
    private int errorLoadingHeight;
    
    private boolean drawBorder;
    
    public ImageBrowserItemField(IImageBrowserItemModel model, boolean isFocusable, boolean drawBorder) {
        super(isFocusable ? Field.FOCUSABLE : Field.NON_FOCUSABLE);
        imgUrl = model.getImgUrl();
        label  = model.getLabel();
        this.drawBorder = drawBorder;
    }
    
    public ImageBrowserItemField(IImageBrowserItemModel model) {
        this(model, true, true);
    }
    
    void setDimention(int width, int height) {
        if (w != width || h != height) {
            // dimensions changed - probably screen was rotated, so need to reload Bitmap
            bmp = null;
            isLoadingImage = false;
        }
        
        w = width;
        h = height;
    }
    
    public int getPreferredHeight() {
        return h;
    }
    
    public int getPreferredWidth() {
        return w;
    }
    
    protected void layout(int width, int height) {
        // Logger.debug(this, "layout: entered for '" + label + "', width = " + 
        // width + ", height = " + height);
        
        // ignore params, just use our custom w and h, that were previously set at setDimention()
        setExtent(w, h);
    }

    protected void paint(Graphics gfx) {
        // Logger.debug(this, "paint: entered for '" + label + '\'');

        // draw border
        if (drawBorder) {
            final int initialColor = gfx.getColor();
            gfx.setColor(Color.GRAY);
            gfx.drawLine(w-1, 0, w-1, h-1);
            gfx.drawLine(0, h-1, w-1, h-1);
            gfx.setColor(initialColor);
        }
        
        if (label != null) {
            drawLabel(gfx);
        }
        
        if (imgUrl == null) { 
            return;
        }
        
        if (bmp == null) {
            
            if (errorLoadingImage) {
                drawError(gfx);
                return;
            }
            
            if (!isLoadingImage) {
                desiredImgHeight = h - labelHeight - TOTAL_PADDING_Y;
                desiredImgWidth  = w - TOTAL_PADDING_X;
                isLoadingImage = true;
                
                BitmapLoader.requestLoading(bitmapLoading);
            }
            
            if (isLoadingImage) {
                drawLoading(gfx);
            }
            
        } else {
            drawBitmap(gfx);
        }
        
        // Logger.debug(this, "paint: passed");
    }
    
    protected boolean navigationClick(int status, int time) {
        fieldChangeNotify(0);
        return true;
    }
    
    protected boolean keyChar(char character, int status, int time) {
        if (character == Characters.ENTER) {
            fieldChangeNotify(0);
            return true;
        }
        return super.keyChar(character, status, time);
    }
    
    private void drawError(Graphics gfx) {
        errorLoadingHeight = gfx.getFont().getHeight();
        errorLoadingWidth  = w - TOTAL_PADDING_X;
        
        gfx.drawText(
            ERROR_LABEL, 
            PADDING_LEFT, ((h - labelHeight - errorLoadingHeight - PADDING_BOTTOM) >> 1), 
            (DrawStyle.ELLIPSIS | DrawStyle.HCENTER), 
            errorLoadingWidth
        );
    }
    
    private void drawLoading(Graphics gfx) {
        readingHeight = gfx.getFont().getHeight();
        readingWidth  = w - TOTAL_PADDING_X;
        
        gfx.drawText(
            READING_LABEL, 
            PADDING_LEFT, ((h - labelHeight - readingHeight - PADDING_BOTTOM) >> 1), 
            (DrawStyle.ELLIPSIS | DrawStyle.HCENTER), 
            readingWidth
        );
    }
    
    private void drawLabel(Graphics gfx) {
        labelHeight = gfx.getFont().getHeight();
        labelWidth  = w - TOTAL_PADDING_X;
        
        gfx.drawText(
            label, 
            PADDING_LEFT, (h - labelHeight - PADDING_BOTTOM), 
            (DrawStyle.ELLIPSIS | DrawStyle.HCENTER), 
            labelWidth
        );
    }
    
    private void drawBitmap(Graphics gfx) {
        final int width  = bmp.getWidth();
        final int height = bmp.getHeight();
        
        final int x = PADDING_LEFT + ((desiredImgWidth  - width)  >> 1);
        final int y = PADDING_TOP  + ((desiredImgHeight - height) >> 1);
        
        gfx.drawBitmap(x, y, width, height, bmp, 0, 0);
        
        final int initialColor = gfx.getColor();
        gfx.setColor(Color.GRAY);
        gfx.drawRect(x, y, width, height);
        gfx.setColor(initialColor);
    }
    
    private IBitmapLoading bitmapLoading = new IBitmapLoading() {
        
        public int getDesiredImgHeight() {
            return desiredImgHeight;
        }
        
        public int getDesiredImgWidth() {
            return desiredImgWidth;
        }
        
        public String getImgUrl() {
            return imgUrl;
        }
        
        public void setBitmap(final Bitmap b) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    bmp = b;
                    isLoadingImage = false;
                    invalidate();
                }
            });
        }

        public void fail() {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    errorLoadingImage = true;
                    isLoadingImage = false;
                    invalidate();
                }
            });
        }
    };
}