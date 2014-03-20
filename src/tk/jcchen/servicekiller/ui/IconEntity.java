package tk.jcchen.servicekiller.ui;

import android.graphics.drawable.Drawable;

public class IconEntity {

	private String name;
	private Drawable image;
	private String packageName;
	private boolean isSelected;
	
	public IconEntity(String name, Drawable image, String packageName) {
		super();
		this.name = name;
		this.image = image;
		this.packageName = packageName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}