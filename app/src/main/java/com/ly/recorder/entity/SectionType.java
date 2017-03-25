package com.ly.recorder.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by ly on 2017/3/23 17:51.
 */

public class SectionType extends SectionEntity<Type> {


    public SectionType(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SectionType(Type type) {
        super(type);
    }
}
