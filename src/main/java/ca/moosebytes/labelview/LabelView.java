package ca.moosebytes.labelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
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


    private int lineColour;
    private int textColour;
    private int labelColour;
    private String textValue;
    private String labelValue;
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
            lineColour = ContextCompat.getColor(context, DEFAULT_LINE_COLOR);
            textColour = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR);
            lineThickness = DEFAULT_LINE_THICKNESS;
        }

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColour);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineThickness);
        linePaint.setStrokeCap(Paint.Cap.SQUARE);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/copse.ttf"));
        textPaint.setColor(textColour);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);

        labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/copse.ttf"));
        labelPaint.setColor(labelColour);
        labelPaint.setTextSize(labelSize);

        textMetrics = textPaint.getFontMetrics();
        labelMetrics = labelPaint.getFontMetrics();

        textBounds = new Rect();
        labelBounds = new Rect();



    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
        invalidate();
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
        invalidate();
    }

    private void updateLineYPosition(int height) {
        lineY = height / 2;
    }

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

    private void updateTextPosition(Rect textBounds, int width) {
        textY = (int) ((lineY - linePaint.getStrokeWidth() / 2) - textMetrics.descent);
        textX = width / 2 - textBounds.centerX();
    }

    private void updateLabelPosition(Rect labelBounds, int width) {
        labelY = (int) ((lineY + linePaint.getStrokeWidth() / 2) - labelMetrics.ascent);
        labelX = width / 2 - labelBounds.centerX();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        textPaint.getTextBounds(textValue, 0, textValue.length(), textBounds);
        labelPaint.getTextBounds(labelValue, 0, labelValue.length(), labelBounds);
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
