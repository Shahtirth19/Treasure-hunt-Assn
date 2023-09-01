package com.example.mdaassn.modalclass;

import java.util.List;

public class ParentModalClass {
    String title,id;
    List<ChildModalClass> childModalClassList;

    public ParentModalClass(String title, String id, List<ChildModalClass> childModalClassList) {
        this.title = title;
        this.id = id;
        this.childModalClassList = childModalClassList;
    }
}
