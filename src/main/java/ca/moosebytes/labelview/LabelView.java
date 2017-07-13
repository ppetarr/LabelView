package ca.moosebytes.labelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * Created by Petar on 6/21/2017.
 */

public class LabelView extends View {

    private final int DEFAULT_LINE_COLOR = R.color.default_line_color;
    private final int DEFAULT_TEXT_COLOR = R.color.default_text_color;
    private final float DEFAULT_LINE_THICKNESS = 4f;
    private final float DEFAULT_TEXT_SIZE = 15 * getResources().getDisplayMetrics().scaledDensity;


    private Paint linePaint;
    private TextPaint textPaint;
    private TextPaint labelPaint;

    private Paint.FontMetrics textMetrics;
    private Paint.FontMetrics labelMetrics;

    private Rect textBounds;
    private Rect labelBounds;

    private int lineY;
    private int textY;
    private int labelY;

    private int lineStartX;
    private int lineEndX;

    private int textX;
    private int labelX;

    private String textValue;
    private String labelValue;
    private int lineColour;
    private int textColour;
    private int labelColour;
    private float lineThickness;
    private int labelSize;
    private int textSize;

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LabelView(Context context) {
        super(context);
        init(context, null);

    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabelView, 0, 0);
            try {
                textValue = a.getString(R.styleable.LabelView_textValue);
                labelValue = a.getString(R.styleable.LabelView_labelValue);
                lineColour = a.getColor(R.styleable.LabelView_lineColour, ContextCompat.getColor(context, DEFAULT_LINE_COLOR));
                textColour = a.getColor(R.styleable.LabelView_textColour, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR));
                labelColour = a.getColor(R.styleable.LabelView_labelColour, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR));
                lineThickness = a.getFloat(R.styleable.LabelView_lineThickness, 4f);
                labelSize = a.getDimensionPixelSize(R.styleable.LabelView_labelSize, (int) DEFAULT_TEXT_SIZE);
                textSize = a.getDimensionPixelSize(R.styleable.LabelView_textSize, (int) DEFAULT_TEXT_SIZE);
            } finally {
                a.recycle();
            }
        } else {
            textSize = labelSize = (int) DEFAULT_TEXT_SIZE;
            lineColour = ContextCompat.getColor(context, DEFAULT_LINE_COLOR);
            textColour = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR);
            labelColour = textColour;
            lineThickness = DEFAULT_LINE_THICKNESS;
        }

        if (textValue == null) textValue = "";
        if (labelValue == null) labelValue = "";

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColour);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineThickness);
        linePaint.setStrokeCap(Paint.Cap.SQUARE);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(textColour);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);

        labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(labelColour);
        labelPaint.setTextSize(labelSize);
        textMetrics = textPaint.getFontMetrics();
        labelMetrics = labelPaint.getFontMetrics();

        textBounds = new Rect();
        labelBounds = new Rect();
    }

    /**
     * Sets the text that will appear above the line.
     * @param textValue String value.
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
        if (textValue == null) this.textValue = "";
        requestLayout();
        invalidate();
    }

    /**
     * Sets the label text that will appear below the line.
     * @param labelValue String value.
     */
    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
        if (labelValue == null) this.labelValue = "";
        requestLayout();
        invalidate();
    }

    /**
     * Sets the text size as SP (scaled pixel unit).
     * @param textSize desired SP size.
     */
    public void setTextSize(float textSize) {
        setRawTextSize(textPaint, textSize);
    }

    /**
     * Sets the label size as SP (scaled pixel unit).
     * @param labelSize desired SP size.
     */
    public void setLabelSize(float labelSize) {
        setRawTextSize(labelPaint, labelSize);
    }

    private void setRawTextSize(TextPaint paint, float textSize) {
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics()));
        requestLayout();
        invalidate();
    }

    /**
     * Sets the line colour.
     * @param colour int specified colour.
     */
    public void setLineColour(@ColorInt int colour) {
        this.lineColour = colour;
        linePaint.setColor(colour);
        invalidate();
    }

    /**
     * Sets the line thickness.
     * @param lineThickness float value.
     */
    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
        linePaint.setStrokeWidth(lineThickness);
        invalidate();
    }

    /**
     * Update y-position of the line.
     * @param height View height.
     */
    private void updateLineYPosition(int height) {
        lineY = height / 2;
    }

    /**
     * Update the x-position of the line. <p />
     * The length of the line is based on the longest text between text and label.
     * @param textBounds Rect bounds based on the text value.
     * @param labelBounds Rect bounds based on the label value.
     */
    private void updateLineXPosition(Rect textBounds, Rect labelBounds) {
        boolean textLonger = textBounds.width() > labelBounds.width();

        if (textLonger) {
            lineStartX = textX - textBounds.left;
            lineEndX = textX + textBounds.width();
        } else {
            lineStartX = labelX - labelBounds.left;
            lineEndX = labelX + labelBounds.width();
        }
    }

    /**
     * Update (x,y) positions of text.
     * @param textBounds Rect bounds based on text value.
     * @param width View width.
     */
    private void updateTextPosition(Rect textBounds, int width) {
        textY = (int) ((lineY - linePaint.getStrokeWidth() / 2) - textMetrics.descent);
        textX = width / 2 - textBounds.centerX();
    }

    /**
     * Update (x,y) positions of label.
     * @param labelBounds Rect bounds based on label value.
     * @param width View width.
     */
    private void updateLabelPosition(Rect labelBounds, int width) {
        labelY = (int) ((lineY + linePaint.getStrokeWidth() / 2) - labelMetrics.ascent);
        labelX = width / 2 - labelBounds.centerX();
    }

    /**
     * Measure the minimum width required to display the view. <br />
     * Based on the longest String value width-wise.
     *
     * <p><strong>NOTE: View padding is not taken into account</strong></p>
     * @param textBounds Rect bounds based on text value.
     * @param labelBounds Rect bounds based on label value.
     * @return measured minimum width.
     */
    private int measureMinimumWidth(Rect textBounds, Rect labelBounds) {
        return textBounds.width() > labelBounds.width() ? textBounds.width() : labelBounds.width();
    }

    /**
     * Measure the minimum height required to display the view. <br />
     * Based on FontMetrics of text value and label value in addition to the line stroke width.
     *
     * <p><strong>NOTE: View padding is not taken into account.</strong></p>
     * @param textMetrics FontMetrics based on the text value.
     * @param labelMetrics FontMetrics based on the label value.
     * @param linePaint Paint used to draw the dividing line
     * @return measured minimum height.
     */
    private int measureMinimumHeight(Paint.FontMetrics textMetrics, Paint.FontMetrics labelMetrics, Paint linePaint) {
        return (int) ((textMetrics.bottom - textMetrics.top) + (labelMetrics.bottom - labelMetrics.top) + linePaint.getStrokeWidth());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        textPaint.getTextBounds(textValue, 0, textValue.length(), textBounds);
        labelPaint.getTextBounds(labelValue, 0, labelValue.length(), labelBounds);

        int minimumWidth = measureMinimumWidth(textBounds, labelBounds);
        int minimumHeight = measureMinimumHeight(textMetrics, labelMetrics, linePaint);

        int width = reconcileSize(minimumWidth, widthMeasureSpec);
        int height = reconcileSize(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    private int reconcileSize(int size, int measureSpec) {
        switch (measureSpec) {
            case MeasureSpec.EXACTLY:
                return MeasureSpec.getSize(measureSpec);
            case MeasureSpec.AT_MOST:
                return Math.min(size, MeasureSpec.getSize(measureSpec));
            case MeasureSpec.UNSPECIFIED:
                default:
                return size;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateLineYPosition(height);
        updateTextPosition(textBounds, width);
        updateLabelPosition(labelBounds, width);
        updateLineXPosition(textBounds, labelBounds);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, linePaint);
        canvas.drawText(labelValue, labelX, labelY, labelPaint);
        canvas.drawText(textValue, textX, textY, textPaint);
    }

}
