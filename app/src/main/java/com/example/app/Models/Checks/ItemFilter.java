package com.example.app.Models.Checks;

public class ItemFilter extends BaseFilter {

    private boolean checked;

    public ItemFilter() {}

    public ItemFilter(String name){
        super(name);
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
