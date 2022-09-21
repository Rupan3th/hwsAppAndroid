package com.yingjie.addressselector.inner;

public class SelectorFactory {

    private static ISelector iSelector = new SelectorImp();

    public SelectorFactory() {}

    public static ISelector create() {
        return iSelector;
    }
}
