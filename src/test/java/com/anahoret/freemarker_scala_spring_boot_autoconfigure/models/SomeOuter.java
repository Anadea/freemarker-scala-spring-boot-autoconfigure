package com.anahoret.freemarker_scala_spring_boot_autoconfigure.models;

public class SomeOuter {

    public SomeInterface getSomeInterface() {
        return new SomeInner();
    }

    private static class SomeInner implements SomeInterface {
        @Override
        public String getSomething() {
            return "Something";
        }
    }

}
