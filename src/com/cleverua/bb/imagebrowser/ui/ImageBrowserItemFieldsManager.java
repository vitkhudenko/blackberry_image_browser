package com.cleverua.bb.imagebrowser.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.FlowFieldManager;

public class ImageBrowserItemFieldsManager extends FlowFieldManager {

    private int columns;
    private int rows;
    private int yOffset;
    
    public ImageBrowserItemFieldsManager(int columns, int rows, int yOffset) {
        super(0);
        this.columns = (columns == 0) ? 1 : columns;
        this.rows    = (rows == 0)    ? 1 : rows;
        this.yOffset = yOffset;
    }
    
    protected void sublayout(int width, int height) {
        final int fieldWidth  = Math.min(Display.getWidth(), width) / columns;
        final int fieldHeight = (Math.min(Display.getHeight(), height) - yOffset) / rows;
        
        final int fieldsCount = getFieldCount();
        
        for (int i = 0; i < fieldsCount; i++) {
            ((ImageBrowserItemField) getField(i)).setDimention(fieldWidth, fieldHeight);
        }
        
        super.sublayout(width, height);
    }
}