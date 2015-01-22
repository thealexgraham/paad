package net.alexgraham.thesis.tests.demos.mvc;

import java.awt.Font;

public class TextElementModel extends AbstractModel
{


	private String text;
    private Font font;
    private Integer x;
	private Integer y;
    private Integer opacity;
    private Integer rotation;


    /**
     * Provides the means to set or reset the model to
     * a default state
     */
    public void initDefault() {

        setOpacity(89);
        setRotation(0);
        setText("Sample Text");
        setFont(new Font("Arial", Font.BOLD, 24));
        setX(50);
        setY(50);

    }

    //  Accessors


    public String getText() {
        return text;
    }

    public void setText(String text) {

        String oldText = this.text;
        this.text = text;

        firePropertyChange(
            DefaultController.ELEMENT_TEXT_PROPERTY,
            oldText, text);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {

        Font oldFont = this.font;
        this.font = font;

        firePropertyChange(
            DefaultController.ELEMENT_FONT_PROPERTY,
            oldFont, font);
    }

    public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getOpacity() {
		return opacity;
	}

	public void setOpacity(Integer opacity) {
		this.opacity = opacity;
	}

	public Integer getRotation() {
		return rotation;
	}

	public void setRotation(Integer rotation) {
		this.rotation = rotation;
	}

}
