package com.anahoret.freemarker_scala_spring_boot_autoconfigure.models;

import com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper.ScalaWrapper;
import freemarker.template.TemplateModel;

public class SomeOuter {

    public SomeInterface getSomeInterface() {
        return new SomeInner();
    }

    public TemplateModel wrappedArray(ScalaWrapper scalaWrapper) {
        return scalaWrapper.wrap(new String[]{"One", "Two"});
    }

    private static class SomeInner implements SomeInterface {
        @Override
        public String getSomething() {
            return "Something";
        }
    }

}
