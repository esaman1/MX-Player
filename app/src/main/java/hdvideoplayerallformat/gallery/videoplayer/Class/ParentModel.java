package hdvideoplayerallformat.gallery.videoplayer.Class;

import java.util.List;

public class ParentModel {

    private String ParentItemTitle;
    private List<BaseModel> ChildItemList;

    // Constructor of the class
    // to initialize the variables
    public ParentModel(
            String ParentItemTitle,
            List<BaseModel> ChildItemList) {

        this.ParentItemTitle = ParentItemTitle;
        this.ChildItemList = ChildItemList;
    }

    // Getter and Setter methods
    // for each parameter
    public String getParentItemTitle() {
        return ParentItemTitle;
    }

    public void setParentItemTitle(
            String parentItemTitle) {
        ParentItemTitle = parentItemTitle;
    }

    public List<BaseModel> getChildItemList() {
        return ChildItemList;
    }

    public void setChildItemList(
            List<BaseModel> childItemList) {
        ChildItemList = childItemList;
    }

}
